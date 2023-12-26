package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderResponseDTO {

    private Long id;
    private String title;
    private Long userId;
    private String nickname;
    private String folderImg;
    private String profileImage;
    private boolean hideFlag;

    private int followFlag;
    private int pinCount;


    public FolderResponseDTO(Folder folder) {
        this.id = folder.getId();
        this.title = folder.getTitle();
        this.userId = folder.getUser().getId();
        this.nickname = folder.getUser().getNickname();
        this.folderImg = folder.getFolderImg();
        this.profileImage = folder.getUser().getProfileImage();
        this.hideFlag = folder.isHideFlag();
        this.pinCount = folder.getPins().size();
    }
}
