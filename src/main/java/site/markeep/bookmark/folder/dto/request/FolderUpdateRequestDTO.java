package site.markeep.bookmark.folder.dto.request;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.tag.entity.Tag;

import java.util.List;

@Setter @Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderUpdateRequestDTO {

    private Long folderId;
    private String title;
    private boolean hideFlag;
    private List<String> tags;

}
