package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import org.springframework.http.HttpHeaders;
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FolderAllResponseDTO {

    private Long folderId;
    private String title;
    private String nickname;

}
