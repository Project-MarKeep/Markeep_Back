package site.markeep.bookmark.user.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Value;
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
import site.markeep.bookmark.user.dto.KakaoUserDTO;
import site.markeep.bookmark.user.dto.SnsLoginDTO;
import site.markeep.bookmark.user.dto.request.GoogleLoginRequestDTO;
import site.markeep.bookmark.user.dto.request.JoinRequestDTO;
import site.markeep.bookmark.user.dto.request.LoginRequestDTO;
import site.markeep.bookmark.user.dto.request.PasswordUpdateRequestDTO;
import site.markeep.bookmark.user.dto.response.LoginResponseDTO;
import site.markeep.bookmark.user.entity.Role;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRefreshTokenRepository;
import site.markeep.bookmark.user.repository.UserRepository;
import site.markeep.bookmark.user.repository.UserRepositoryImpl;

import javax.persistence.EntityManager;
import java.util.Map;

import static site.markeep.bookmark.user.entity.QUser.user;

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
    private final UserRepositoryImpl repoimpl;
    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Value("${kakao.client_id}")
    private String KAKAO_CLIENT_ID;

    @Value("${kakao.client_secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${kakao.redirect_uri")
    private String KAKAO_REDIRECT_URI;

    @Value("${naver.client_id}")
    private String NAVER_CLIENT_ID;

    @Value("${naver.client_secret}")
    private String NAVER_CLIENT_SECRET;

    @Value("${naver.state")
    private String NAVER_STATE;

    @Value("${google.client_id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${google.client_secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${google.scope}")
    private String GOOGLE_SCOPE;

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

        // 로그인 성공한 유저에게 제공할 액세스 토큰 생성
        String accessToken = tokenProvider.createAccessToken(user);
        log.info("액세스 토큰 생성 됨");
        log.info("액세스 토큰 : {}", accessToken);
        // 자동로그인 체크 + 로그인 성공한 유저에게 제공할 리프레시 토큰 생성
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
                .autoLogin(dto.isAutoLogin())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
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
    
    public void updatePassword(PasswordUpdateRequestDTO dto) {
        repoimpl.updatePassword(dto);
    }
    public LoginResponseDTO naverLogin(final SnsLoginDTO requestDTO) {
        Map<String, Object> responseData = getNaverAccessToken(requestDTO.getCode());
        log.info("token: {}", responseData.get("access_token"));


        Map<String, String> userInfo = getNaverUserInfo(responseData.get("access_token"));

        String refreshToken = null;

        // 사용자가 자동 로그인 체크했을 경우
        if(requestDTO.isAutoLogin()) refreshToken = tokenProvider.createRefreshToken();

        // 중복되지 않았을 경우
        if(!isDuplicate(userInfo.get("response/email"))){
            userRepository.save(User.builder()
                            .email(userInfo.get("response/email"))
                            .password("password!")
                            .nickname(userInfo.get("response/nickname"))
                            .refreshToken(refreshToken)
                            .build()
                    );
        }

        // 이미 가입돼 있는 경우
        User foundUser = userRepository.findByEmail(userInfo.get("response/email")).orElseThrow();

        String accessToken = tokenProvider.createAccessToken(foundUser);

        return new LoginResponseDTO(foundUser, accessToken, refreshToken);

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

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // 요청 바디 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", NAVER_CLIENT_ID);
        params.add("client_secret", NAVER_CLIENT_SECRET);
        params.add("code", code);
        params.add("state", NAVER_STATE);
        params.add("service_provider", "NAVER");

        RestTemplate template = new RestTemplate();

        ResponseEntity<Map> ResponseEntity = template.exchange(requestUri, HttpMethod.POST, new HttpEntity<>(params, headers), Map.class);

        Map<String, Object> responseData =  (Map<String, Object>)ResponseEntity.getBody();
        log.info("토큰 요청 응답 데이터! - {}", responseData);

        return responseData;

    }

    // 구글로그인 성공하면 코드 받아서 여기로 넘어옴
    public LoginResponseDTO googleLogin(GoogleLoginRequestDTO dto) {

        String googoleUserEmail = dto.getEmail();
        String googleUserNickname = dto.getNickname();

        // sns로그인 유저에게 지급할 액세스 토큰 생성
        String accessToken = tokenProvider.createAccessToken(User.builder()
                .email(dto.getEmail())
                .nickname(dto.getNickname())
                .role(Role.USER)
                .build());
        // sns로그인 유저에게 지급할 리프레시 토큰 생성
        String refreshToken = tokenProvider.createRefreshToken();

        // 먼저 이미 있는 이메일인지 확인
        if(!userRepository.findByEmail(googoleUserEmail).isPresent()){
            // DB에 없는 이메일이라면
            if (dto.isAutoLogin()) {
                // 자동로그인 체크 한 사람이라면
                // log.warn("여기지금 이메일 없고 자동로그인 체크 OOOOOOOO 사람이야!!!!!!!!!!!!!!!!!!!!");
                User googleLoginUser = userRepository.save(User.builder()
                        .email(googoleUserEmail)
                        .nickname(googleUserNickname)
                        .password("password")
                        .autoLogin(dto.isAutoLogin())
                        .refreshToken(refreshToken)
                        .build());
                return LoginResponseDTO.builder()
                        .id(userRepository.findByEmail(googoleUserEmail).get().getId())
                        .email(googoleUserEmail)
                        .nickname(googleUserNickname)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();
            }else {
                // log.warn("여기지금 이메일 없고 자동로그인 체크 XXXXXX 사람이야!!!!!!!!!!!!!!!!!!!!");
                // 자동로그인 체크 안한사람이라면
                userRepository.save(User.builder()
                        .email(googoleUserEmail)
                        .nickname(googleUserNickname)
                        .password("password")
                        .autoLogin(dto.isAutoLogin())
                        .build());
                return LoginResponseDTO.builder()
                        .id(userRepository.findByEmail(googoleUserEmail).get().getId())
                        .email(googoleUserEmail)
                        .nickname(googleUserNickname)
                        .accessToken(accessToken)
                        .build();
            }
        }else{ // DB에 있는 이메일이라면
            if(dto.isAutoLogin()){
                // log.warn("여기는 이메일 등록 되어있고!!!!! 자동로그인 체크 OOOOOO 한 사람야!!!!!!!!!!!!!!!");
                // 자동로그인 체크 한 사람이라면 -> 자동 로그인 값과 리프레시토큰 업데이트
                queryFactory.update(user)
                        .set(user.autoLogin, dto.isAutoLogin())
                        .set(user.refreshToken, refreshToken)
                        .where(user.email.eq(dto.getEmail()))
                        .execute();
                em.flush();
                em.clear();
                return LoginResponseDTO.builder()
                        .id(userRepository.findByEmail(googoleUserEmail).get().getId())
                        .email(googoleUserEmail)
                        .nickname(googleUserNickname)
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build();

            }else {
                // log.warn("여기는 이메일 등록 되어있고!!!!! 자동로그인 체크 XXXXXXXX 한 사람야!!!!!!!!!!!!!!!");
                // 자동로그인 체크 안 한 사람이라면 -> 자동로그인 값만 업데이트
                queryFactory.update(user)
                        .set(user.autoLogin, dto.isAutoLogin())
                        .where(user.email.eq(dto.getEmail()));
                em.flush();
                em.clear();
                return LoginResponseDTO.builder()
                        .id(userRepository.findByEmail(googoleUserEmail).get().getId())
                        .email(googoleUserEmail)
                        .nickname(googleUserNickname)
                        .accessToken(accessToken)
                        .build();

            }
        }
    }

    public LoginResponseDTO kakaoService(SnsLoginDTO requestDTO) {

        Map<String, Object> responseData = kakaoGetAccessToken(requestDTO.getCode());
        String token = responseData.get("access_token").toString();
        KakaoUserDTO responseDTO = getKakaoUserInfo(token);

        String refreshToken = null;
        if(requestDTO.isAutoLogin()) refreshToken = tokenProvider.createRefreshToken();



        if(!isDuplicate(responseDTO.getKakaoAccount().getEmail())){
            User saved = userRepository.save(responseDTO.toEntity(refreshToken));
        }

        User foundUser = userRepository.findByEmail(responseDTO.getKakaoAccount().getEmail()).orElseThrow();

        String accessToken = tokenProvider.createAccessToken(foundUser);

        return new LoginResponseDTO(foundUser, accessToken, refreshToken);
    }

    private KakaoUserDTO getKakaoUserInfo(String token) {
        String requestUri = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        RestTemplate template = new RestTemplate();

        ResponseEntity<KakaoUserDTO> responseEntity = template.exchange(requestUri, HttpMethod.GET, new HttpEntity<>(headers), KakaoUserDTO.class);

        KakaoUserDTO responseData = responseEntity.getBody();

        return responseData;

    }

    public Map<String, Object> kakaoGetAccessToken(String code) {
        String requestUri = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant-type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);
        params.add("client_secret", KAKAO_CLIENT_SECRET);

        RestTemplate template = new RestTemplate();

        ResponseEntity<Map> responseEntity = template.exchange(requestUri, HttpMethod.POST, new HttpEntity<>(headers, params), Map.class);

        Map<String, Object> responseData = (Map<String, Object>) responseEntity.getBody();

        return responseData;


    }
}

