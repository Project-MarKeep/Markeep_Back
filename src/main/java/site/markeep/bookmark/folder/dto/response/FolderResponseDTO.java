package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;

import java.time.LocalDateTime;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponseDTO {

    private Long id;
    private String title;
    private Long userId;
    private String folderImg;
    private boolean hideFlag;


    public FolderResponseDTO(Folder folder) {
        this.id = folder.getId();
        this.title = folder.getTitle();
        this.userId = folder.getUser().getId();
        this.folderImg = folder.getFolderImg();
        this.hideFlag = folder.isHideFlag();
    }

}