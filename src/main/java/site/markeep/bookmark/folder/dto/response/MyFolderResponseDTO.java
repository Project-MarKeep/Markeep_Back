package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyFolderResponseDTO {

    private Long id;
    private String title;
    private Long userId;
    private String folderImg;
    private boolean hideFlag;
    private int pinCount;

    public MyFolderResponseDTO(Folder folder) {
        this.id = folder.getId();
        this.title = folder.getTitle();
        this.userId = folder.getUser().getId();
        this.folderImg = folder.getFolderImg();
        this.hideFlag = folder.isHideFlag();
        this.pinCount = folder.getPins().size();

    }
}
