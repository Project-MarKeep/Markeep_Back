package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.util.dto.page.PageResponseDTO;

import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FolderListResponseDTO {

    private int count; //총 폴더 수
    private PageResponseDTO pageInfo; //페이지 랜더링 정보
    private List<FolderAllResponseDTO> folders;//게시물 랜더링 정보
    private List<UserInFolderResponseDTO> users; // 게시물마다의 user객체 정보

}
