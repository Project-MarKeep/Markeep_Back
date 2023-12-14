package site.markeep.bookmark.user.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordUpdateRequestDTO {


    @NotBlank
    private String email;

    @NotBlank
    private String nickname;

    @NotBlank
    @Size(min = 8, max = 50)
    private String password;

}
