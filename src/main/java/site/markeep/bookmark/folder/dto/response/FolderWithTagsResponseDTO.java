package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.tag.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderWithTagsResponseDTO {

    private FolderResponseDTO folder;
    private List<String> tagNames;

    public FolderWithTagsResponseDTO(Folder folder) {
        this.folder = new FolderResponseDTO(folder);
        this.tagNames = folder.getTags().stream()
                .map(Tag::getTagName)
                .collect(Collectors.toList());
    }
}