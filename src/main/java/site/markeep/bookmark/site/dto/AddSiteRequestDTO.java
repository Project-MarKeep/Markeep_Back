package site.markeep.bookmark.site.dto;

import lombok.*;

@Getter @Setter
@ToString @EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddSiteRequestDTO {

    private String siteName;
    private String url;
    private String comment;
}
