package site.markeep.bookmark.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import site.markeep.bookmark.auth.NewRefreshToken;
import site.markeep.bookmark.auth.TokenProvider;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.user.dto.request.JoinRequestDTO;
import site.markeep.bookmark.user.dto.request.LoginRequestDTO;
import site.markeep.bookmark.user.dto.response.LoginResponseDTO;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRefreshTokenRepository;
import site.markeep.bookmark.user.repository.UserRepository;

import java.util.Map;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;

    private final FolderRepository folderRepository;

    private final UserRefreshTokenRepository userRefreshTokenRepository;

    private final TokenProvider tokenProvider;

    private final BCryptPasswordEncoder encoder;

    @Value("{naver.client_id}")
    private String NAVER_CLIENT_ID;

    @Value("{naver.client_secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("{naver.state")
    private String NAVER_STATE;

    public LoginResponseDTO login(LoginRequestDTO dto) throws Exception {

        log.info("로그인 서비스로 넘어옴");

        // 1. dto에서 이메일 값을 뽑아서 가입 여부 확인
        User user = userRepository
                .findByEmail(dto.getEmail())
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다! 회원 가입을 진행해주세요.")
                );

        log.info("서비스 - dto에서 이메일 비교 성공함");

        // 2. 회원이 맞다면 -> 비밀번호 일치 확인
        String password = dto.getPassword();
        String encodedPassword = user.getPassword();
        if (!encoder.matches(password, encodedPassword)) {
            throw new RuntimeException("비밀번호를 다시 입력해주세요!");
        }

        log.info("서비스 - dto에서 암호화 된 비번 비교 성공");

        String accessToken = tokenProvider.createAccessToken(user);
        log.info("액세스 토큰 : {}", accessToken);
        log.info("액세스 토큰 생성 됨");
        String refreshToken = tokenProvider.createRefreshToken();
        log.info("리프레시 토큰 : {}", refreshToken);
        log.info("리프레시 토큰 생성 됨");

        userRefreshTokenRepository.findById(user.getId())
                .ifPresentOrElse(
            it -> it.updateRefreshToken(refreshToken),
            () -> userRefreshTokenRepository.save(new NewRefreshToken(user, refreshToken))
                );
        userRepository.save(User.builder()
                        .id(user.getId())
                        .password(encodedPassword)
                        .nickname(user.getNickname())
                        .email(dto.getEmail())
                        .joinDate(user.getJoinDate())
                        .autoLogin(dto.isAutoLogin())
                        .refreshToken(refreshToken)
                .build());

        // 이거는 이메일 & 비밀번호 둘 다 일치한 경우 화면단으로 보내는 유저의 정보
        return LoginResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .autoLogin(dto.isAutoLogin())
                .build();
    }

    public void join(JoinRequestDTO dto) {

        String encodedPassword = encoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);

        User saved = userRepository.save(dto.toEntity(dto));
        folderRepository.save(
                Folder.builder()
                    .creator(saved.getId())
                    .user(saved)
                    .title("기본 폴더")
                    .build());
    }

    public boolean isDuplicate(String email) {
        return  userRepository.findByEmail(email).isPresent();
    }


    public LoginResponseDTO naverLogin(final String code) {
        Map<String, Object> responseData = getNaverAccessToken(code);
        log.info("token: {}", responseData.get("access_token"));


        Map<String, String> userInfo = getNaverUserInfo(responseData.get("access_token"));

        // 중복되지 않았을 경우
        if(!isDuplicate(userInfo.get("response/email"))){
            userRepository.save(User.builder()
                            .email(userInfo.get("response/email"))
                            .password("password!")
                            .nickname(userInfo.get("response/nickname"))
                            .build()
                    );
        }

        // 이미 가입돼 있는 경우
        User foundUser = userRepository.findByEmail(userInfo.get("email")).orElseThrow();

        String token = tokenProvider.createAccessToken(foundUser);

        return new LoginResponseDTO(foundUser, token);

    }

    private Map<String, String> getNaverUserInfo(Object accessToken) {
        // 요청 uri
        String requestUri = "https://openapi.naver.com/v1/nid/me";

        // 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        RestTemplate template = new RestTemplate();
        ResponseEntity<Map> responseEntity = template.exchange(requestUri, HttpMethod.POST, new HttpEntity<>(headers), Map.class);

        Map<String, String> responseData = (Map<String, String>) responseEntity.getBody();

        return responseData;

    }

    private Map<String, Object> getNaverAccessToken(String code) {

        // 요청 uri 설정
        String requestUri = "https://nid.naver.com/oauth2.0/token";

        // 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("client_secret", NAVER_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", NAVER_STATE);

        RestTemplate template = new RestTemplate();

        ResponseEntity<Map> ResponseEntity = template.exchange(requestUri, HttpMethod.POST, new HttpEntity<>(params), Map.class);

        Map<String, Object> responseData =  (Map<String, Object>)ResponseEntity.getBody();
        log.info("토큰 요청 응답 데이터! - {}", responseData);

        return responseData;


    }
}

