package site.markeep.bookmark.user.dto;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SnsLoginDTO {

    private String code;
    private boolean autoLogin;
}
