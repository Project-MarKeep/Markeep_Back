package site.markeep.bookmark.site.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSiteInfoResponseDTO {

    //먼저 누구의, 어느 폴더의, 어떤 사이트인지 정보 되돌려주기
    // 이게 필요한가
    private Long userId;

    private Long folderId;

    private Long siteId;
    
    // 이건 사이트 상세 페이지 들어오자마자 유저가 볼 저장 되어 있던 값
    private String siteName;

    private String comment;

}
