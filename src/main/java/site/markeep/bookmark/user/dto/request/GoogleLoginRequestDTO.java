package site.markeep.bookmark.user.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GoogleLoginRequestDTO {

    private String email;

    private  String nickname;

    private boolean autoLogin;

}
