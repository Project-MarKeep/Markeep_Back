package site.markeep.bookmark.site.dto.request;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSiteRequestDTO {

    private Long folderId;
    private String siteName;
    private String url;
    private String comment;

}
