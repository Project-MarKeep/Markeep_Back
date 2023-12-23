package site.markeep.bookmark.site.dto.request;

import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteDeleteRequestDTO {
    private Long folderId;
    private Long siteId;
}
