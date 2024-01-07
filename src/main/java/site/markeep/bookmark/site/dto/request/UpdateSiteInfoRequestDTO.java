package site.markeep.bookmark.site.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSiteInfoRequestDTO {

    private Long folderId;
    private Long siteId;
    private String siteName;
    private String comment;

}
