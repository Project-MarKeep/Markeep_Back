package site.markeep.bookmark.util.dto.page;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Page;
import site.markeep.bookmark.folder.entity.Folder;

@Setter @Getter
@ToString
public class PageResponseDTO {

    private int startPage; //시작 페이지
    private int endPage; // 끝 페이지
    private int currentPage; // 현재 페이지

    private boolean prev; // 이전 버튼
    private boolean next; // 이후 버튼

    private int totalCount; // 총 게시물 수

    // 한 페이지에 배치할 페이지 버튼 수 (1 ~ 10 // 11 ~ 20)
    private static final int PAGE_COUNT = 10;

    public PageResponseDTO(Page<Folder> pageData) {
        // 기존에 사용하셨던 PageCreator랑 다를 게 없어요~
        // 매개값으로 전달된 Page 객체가 많은 정보를 제공하기 때문에 로직이 좀 더 간편해진 것 뿐입니다.
        this.totalCount = (int) pageData.getTotalElements();
        this.currentPage = pageData.getPageable().getPageNumber() + 1;
        this.endPage
                = (int) (Math.ceil((double) currentPage / PAGE_COUNT) * PAGE_COUNT);
        this.startPage = endPage - PAGE_COUNT + 1;

        int realEnd = pageData.getTotalPages();

        if(realEnd < this.endPage) this.endPage = realEnd;

        this.prev = startPage > 1;
        this.next = endPage < realEnd;
    }
}








