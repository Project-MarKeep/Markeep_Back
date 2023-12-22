package site.markeep.bookmark.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.markeep.bookmark.user.entity.Role;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
//@PropertySource(value = "classpath:application.yml", factory = )
public class TokenProvider {

    // 얘는 자체적으로 생성한 서명
    @Value("${jwt.secret}")
    @Autowired
    private String SECRET_KEY;

    @Value("${jwt.access-expiry}")
    private Long ACCESS_EXPIRY;

    @Value("${jwt.refresh-expiry}")
    private Long REFRESH_EXPIRY;

    @Value("${jwt.issuer}")
    private String ISSUER;

    private UserRefreshTokenRepository userRefreshTokenRepository;

    // 여기가 토큰을 진짜로 만드는 메서드
    // 유저가 회원가입 후, 로그인 할 때 토큰을 제공 할 것임

    // accessToken 생성해서 리턴하는 메서드
    public String createAccessToken(User userEntity){
        // 이걸 총 2번 호출해서 accessToken과 refreshToken을 생성해서
        // refreshToken은 DB에 저장하고
        // accessToken은 로컬스토리지에 저장해야 한다.

        // 1. 토큰 만료 시간 설정하기
        Date expiry = Date.from(Instant.now().plus(ACCESS_EXPIRY, ChronoUnit.MINUTES));

        // 기존의 클레임 말고 추가로 더 받아야 하는 값 (= 유저의 이메일 값)
        Map<String, String> claims = new HashMap<>();
        claims.put("email", userEntity.getEmail());
        claims.put("nickname", userEntity.getNickname());
        claims.put("role", userEntity.getRole().toString());

        // 토큰 생성 동시에 리턴
        return Jwts.builder()
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS512
                )
                .setClaims(claims)
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .setSubject(String.valueOf(userEntity.getId()))
                .compact();
    };

    // refreshToken 생성해서 리턴하는 메서드
    public String createRefreshToken(){
        // 이걸 총 2번 호출해서 accessToken과 refreshToken을 생성해서
        // refreshToken은 DB에 저장하고
        // accessToken은 로컬스토리지에 저장해야 한다.

        // 1. 토큰 만료 시간 설정하기
        Date expiry = Date.from(Instant.now().plus(REFRESH_EXPIRY, ChronoUnit.HOURS));

        // 기존의 클레임 말고 추가로 더 받아야 하는 값 (= 유저의 이메일 값)
//        Map<String, String> claims = new HashMap<>();

        // 토큰 생성 동시에 리턴
        return Jwts.builder()
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes()),
                        SignatureAlgorithm.HS512
                )
                .setIssuer(ISSUER)
                .setIssuedAt(new Date())
                .setExpiration(expiry)
                .compact();
    };

    /**
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 위조 여부를 확인
     * 토큰을 json으로 파싱해서 클레임(토큰 정보)을 리턴
     * @param token
     * @return - 토큰안에 있는 인증된 유저 정보를 반환
     */
    public TokenUserInfo validateAndGetTokenUserInfo(String token){
        // 암호화를 풀어내서(파싱) 객체로 만드는 과정
        Claims claims = Jwts.parserBuilder()
                // 토큰 발급자의 발급 당시의 서명을 넣어줌
                .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes())) // 서명을 바이트 배열로 뽀개서 비교하려고 하는 곳
                // 서명 위조 검사: 위조된 경우 예외가 발생한다.
                // 위조가 되지 않은 경우 payload를 리턴
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Claims라는 객체 타입으로 반환 한다.
        log.info("claim: {}",claims);


        return  TokenUserInfo.builder()
                .id(Long.valueOf(claims.getSubject()))
                .nickname(claims.get("nickname", String.class))
                .email(claims.get("email", String.class))
                .role(Role.valueOf(claims.get("role", String.class)))
                .build();
    }


}