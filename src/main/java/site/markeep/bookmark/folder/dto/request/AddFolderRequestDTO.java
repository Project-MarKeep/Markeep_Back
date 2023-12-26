package site.markeep.bookmark.folder.dto.request;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.user.entity.User;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddFolderRequestDTO {

//    @NotBlank
    private String title;

    private boolean hideFlag;

    private  List<String> tagName;



    public Folder toEntity(AddFolderRequestDTO dto, User user, String uploadedFilePath) {
        return   Folder.builder()
                .title(dto.title)
                .hideFlag(dto.hideFlag)
                .folderImg(uploadedFilePath)
                .user(user)
                .build();
    }

}




