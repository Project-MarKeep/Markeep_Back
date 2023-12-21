package site.markeep.bookmark.site.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.site.dto.request.AddSiteRequestDTO;
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
//        return siteRepository.findAllById()

//        List<Site> siteList = folderRepository.findById(folderId).orElseThrow(
//                () -> new RuntimeException("폴더 안에 등록 된 사이트가 없습니다!")
//        );
//        List<Site> siteList = queryFactory.select(site)
//                .where(folder.id.eq(folderId))
//                .orderBy(site.regdate.desc())
//                .fetch();
//        JPAQuery<Site> siteList = query.select(site)
//                .from(site)
//                .join(site.folder, folder)
//                .where(folder.id.eq(folderId))
//                .orderBy(site.regdate.desc());
//        return (List<Site>) siteList;
//        NumberPath<Long> foundId = QFolder.folder.id;
//        log.warn("=================foundId: {}", foundId);
//
//        List<Site> siteList = queryFactory.selectFrom(site)
//                .where(foundId.eq(folderId))
//                .orderBy(site.regdate.desc())
//                .fetch();
//        return siteList;
//        return siteRepository.findAllById(folderId);
    }
}
