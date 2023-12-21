package site.markeep.bookmark.site.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.site.dto.request.AddSiteRequestDTO;
import site.markeep.bookmark.site.dto.request.SiteDeleteRequestDTO;
import site.markeep.bookmark.site.entity.Site;
import site.markeep.bookmark.site.repository.SiteRepository;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final FolderRepository folderRepository;
    private final JPAQueryFactory queryFactory;
//    private final EntityManager em;
//    JPAQuery<?> query = new JPAQuery<>(em);

    public List<?> addSite(AddSiteRequestDTO dto) {
        Folder foundFolder = folderRepository.findById(dto.getFolderId()).orElseThrow(
                () -> new RuntimeException("없는 폴더입니다. 먼저 폴더를 생성해주세요!")
        );

        siteRepository.save(Site.builder()
                .siteName(dto.getSiteName())
                .url(dto.getUrl())
                .comment(dto.getComment())
                .folder(foundFolder)
                .build());
        return siteRepository.findAll();
    }

    // folderId값 가지고 있는 사이트들 다 불러오기
    public List<Site> getSiteList(Long folderId) {

        List<Site> siteList = folderRepository.findById(folderId)
                .orElseThrow(
                        () -> new RuntimeException("폴더에 등록 된 사이트가 없습니다!")
                ).getSites();
        log.warn("=================사이트 리스트 : {}", siteList);
        return siteList;
    }


    public void deleteSite(Long userId, SiteDeleteRequestDTO dto) {
        folderRepository.findById(dto.getFolderId()).orElseThrow(
                () -> new RuntimeException("없는 폴더입니다. 폴더 번호 확인해주세요!")
        );
        if(userId == null ) {
            throw new RuntimeException("회원 가입 확인해 주세요 ");
        }

        try {
            siteRepository.deleteById(dto.getSiteId());
        } catch (Exception e) {
            throw new RuntimeException("site id가 존재하지 않아 site 삭제에 실패했습니다.");
        }

    }
}