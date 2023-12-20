package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.tag.entity.Tag;
import site.markeep.bookmark.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Builder
public class FolderAllResponseDTO {

    private  Folder folder;
//    private List<Tag> tags = new ArrayList<>();


//    public FolderAllResponseDTO(Folder folder,List<Tag> tag) {
    public FolderAllResponseDTO(Folder folder) {
        this.folder = folder;
//        this.tags = tag;
    }


}
