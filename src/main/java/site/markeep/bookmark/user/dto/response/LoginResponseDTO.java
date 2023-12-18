package site.markeep.bookmark.user.dto.response;


import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    private String accessToken;

    private String refreshToken;

    private boolean autoLogin;


    public LoginResponseDTO(User user, String token) {
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.accessToken = token;
    }
}
