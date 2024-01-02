package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.tag.entity.Tag;
import site.markeep.bookmark.user.entity.User;

import java.util.List;
import java.util.stream.Collectors;

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
    private boolean pinFlag;
    private int followFlag;
    private int pinCount;
    private List<String> tagNames;


    public FolderResponseDTO(Folder folder) {
        this.id = folder.getId();
        this.title = folder.getTitle();
        this.userId = folder.getUser().getId();
        this.nickname = folder.getUser().getNickname();
        this.folderImg = folder.getFolderImg();
        this.profileImage = folder.getUser().getProfileImage();
        this.hideFlag = folder.isHideFlag();
        this.pinFlag = folder.isPinFlag();
        this.pinCount = folder.getPins().size();
        this.tagNames = folder.getTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
    }
}
