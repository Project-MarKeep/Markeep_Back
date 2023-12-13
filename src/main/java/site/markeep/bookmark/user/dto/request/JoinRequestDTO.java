package site.markeep.bookmark.user.dto.request;

import lombok.*;
import site.markeep.bookmark.user.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequestDTO {

    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 8, max = 50)
    private String password;

    @NotBlank
    @Size(min = 2, max = 8)
    private String nickname;

    public User toEntity(JoinRequestDTO dto) {
        return User.builder()
                .email(this.getEmail())
                .nickname(this.getNickname())
                .password(dto.getPassword())
                .build();

    }
}