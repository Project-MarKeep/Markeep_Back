package site.markeep.bookmark.user.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleLoginResponseDTO {

    // 사용자 액세스 토큰
    private String accessToken;

    // 사용자 리프레시 토큰
    private String refreshToken;
    
    // 액세스토큰 만료 값
    private Long accessExpiry;
    
    // 조회하고자 하는 사용자 정보
    private String scope;

    // 토큰 테이터 타입
    private String tokenType;

}
