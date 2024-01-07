package site.markeep.bookmark.folder.dto.request;

import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteIdsRequestDTO {

    List<Long> ids;

}
