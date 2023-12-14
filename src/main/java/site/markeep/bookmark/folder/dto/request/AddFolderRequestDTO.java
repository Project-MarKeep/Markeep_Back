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
        //Folder 를 생성한다.

        return   Folder.builder()
                .title(dto.title)
                .hideFlag(dto.hideFlag)
                .creator(user.getId())
                .folderImg(uploadedFilePath)
                .user(user)
                .build();




//        Folder newFolder = Folder.builder()
//                .title(dto.title)
//                .hideFlag(dto.hideFlag)
//                .user(dto.user)
//                .build();
        //생성된 폴더 아이디를 받아 Tag 를 생성한다.
//        for (String tag : dto.tagName) {
//            if (tag == null) break;
//            Tag.builder()
//                    .folder(newFolder)
//                    .tagName(tag)
//                    .build();
//        }
    }

}




