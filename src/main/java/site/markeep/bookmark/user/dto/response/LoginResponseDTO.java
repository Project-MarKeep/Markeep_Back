package site.markeep.bookmark.user.dto.response;


import lombok.*;
import site.markeep.bookmark.user.entity.User;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDTO {

    private Long id;

    private String email;

    private String nickname;

    private boolean autoLogin;

    private String accessToken;

    private String refreshToken;

    // autoLogin 체크 안한 사람을 위한 생성자
    public LoginResponseDTO(User user, String token) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.accessToken = token;
    }
}
