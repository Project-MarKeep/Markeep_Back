package site.markeep.bookmark.site.dto.response;

import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.site.entity.Site;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteResponseDTO {

    private Long id;

    private String siteName;

    private String url;

    private String comment;

    private LocalDateTime regdate;

//    private Folder folder;

    public SiteResponseDTO(Site site) {
        this.id = site.getId();
        this.siteName = site.getSiteName();
        this.url = site.getUrl();
        this.comment = site.getComment();
        this.regdate = site.getRegdate();
//        this.folder = site.getFolder();
    }
}
