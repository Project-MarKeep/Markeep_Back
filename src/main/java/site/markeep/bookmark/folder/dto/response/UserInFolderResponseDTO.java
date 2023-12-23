package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.user.entity.User;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class UserInFolderResponseDTO {

    private User user;

    public UserInFolderResponseDTO(User user) {
        this.user = user;
    }

}
