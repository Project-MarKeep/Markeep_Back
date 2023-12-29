package site.markeep.bookmark.site.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.site.dto.request.AddSiteRequestDTO;
import site.markeep.bookmark.site.dto.request.SingleSiteInfoRequestDTO;
import site.markeep.bookmark.site.dto.request.SiteDeleteRequestDTO;
import site.markeep.bookmark.site.dto.request.UpdateSiteInfoRequestDTO;
import site.markeep.bookmark.site.dto.response.SiteResponseDTO;
import site.markeep.bookmark.site.entity.QSite;
import site.markeep.bookmark.site.entity.Site;
import site.markeep.bookmark.site.repository.SiteRepository;
import site.markeep.bookmark.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

import static site.markeep.bookmark.site.entity.QSite.site;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class SiteService {

    private final UserRepository userRepository;
    private final SiteRepository siteRepository;
    private final FolderRepository folderRepository;
    private final JPAQueryFactory queryFactory;
//    private final EntityManager em;
//    JPAQuery<?> query = new JPAQuery<>(em);

    public void addSite(TokenUserInfo userInfo, AddSiteRequestDTO dto) {
        Folder foundFolder = folderRepository.findById(dto.getFolderId()).orElseThrow(
                () -> new RuntimeException("없는 폴더입니다. 먼저 폴더를 생성해주세요!")
        );

        log.warn("foundFolder ->");

        siteRepository.save(Site.builder()
                .siteName(dto.getSiteName())
                .url(dto.getUrl())
                .comment(dto.getComment())
                .folder(foundFolder)
                .build());
    }

    // folderId로 사이트 목록 조회
    public List<SiteResponseDTO> getSiteList(Long folderId) {
        log.warn("folderId -> {}", folderId);
        List<Site> siteList = siteRepository.findByFolderId(folderId);
//        List<Site> siteList = queryFactory.selectFrom(site)
//                .where(site.folder.id.eq(folderId))
//                .fetch();
//        List<Site> siteLists = folderRepository.findById(folderId)
//                .orElseThrow(
//                        () -> new RuntimeException("폴더에 등록 된 사이트가 없습니다!")
//                ).getSites();
        log.warn("=================siteList : {}", siteList);
        List<SiteResponseDTO> responseDTOList = siteList.stream()
                .map(site -> new SiteResponseDTO(site))
                .collect(Collectors.toList());
        log.warn("=================responseDTOList : {}", responseDTOList);
        return responseDTOList;
    }


    public void deleteSite(Long userId, SiteDeleteRequestDTO dto) {
        folderRepository.findById(dto.getFolderId()).orElseThrow(
                () -> new RuntimeException("없는 폴더입니다. 폴더 번호 확인해주세요!")
        );
        if(userId == null ) {
            throw new RuntimeException("회원 가입 확인해 주세요 ");
        }
        if(!userId.equals(folderRepository.getFolderUser(dto.getFolderId()))){
            throw new RuntimeException("회원님의 site 가 아닙니다 ");
        }
        try {
            siteRepository.deleteById(dto.getSiteId());
        } catch (Exception e) {
            throw new RuntimeException("site id가 존재하지 않아 site 삭제에 실패했습니다.");
        }

    }

    public void updateRegistSiteInfo(UpdateSiteInfoRequestDTO dto) {
//        log.warn("[SERVICE] updateRegistSiteInfo에 들어온건 맞니ㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣㅣ");
//        log.warn("아니면 dto에서 값 꺼내는건 괜찮나? -> {}",dto.getUserId());
        // 기존의 정보를 입력 되어 있게 해주기 위해서 기존 정보 선언
        Site foundSite = siteRepository.findById(dto.getSiteId()).orElseThrow();
//        log.warn("siteName 오나 보자 : {}", site.getSiteName());
//        log.warn("comment 오나 보자 : {}", site.getComment());

        // udpate시켜주기
        if(dto.getComment() == null) {
            long updateSiteNameOnly = queryFactory.update(site)
                    .set(site.siteName, dto.getSiteName())
                    .set(site.comment, foundSite.getComment())
                    .where(site.id.eq(dto.getSiteId()))
                    .execute();
            log.warn("사이트 이름만 수정한거 : {}", updateSiteNameOnly);
        } else if(dto.getSiteName() == null){
            long execute = queryFactory.update(site)
                    .set(site.siteName, foundSite.getSiteName())
                    .set(site.comment, dto.getComment())
                    .where(site.id.eq(dto.getSiteId()))
                    .execute();
            log.warn("사이트 등록할 때, 코멘트 수정 내용 : {}", execute);
        } else if(dto.getSiteName() != null && dto.getComment() != null){
            long execute = queryFactory.update(site)
                    .set(site.siteName, dto.getSiteName())
                    .set(site.comment, dto.getComment())
                    .where(site.id.eq(dto.getSiteId()))
                    .execute();
            log.warn("둘 다 수정 : {}", execute);
        }
    }

    public SingleSiteInfoRequestDTO getSingleSiteData(Long siteId) {
        log.warn("[SERVICE] - siteId : {}", siteId);
        Site siteInfo = siteRepository.findById(siteId).orElseThrow();
        return SingleSiteInfoRequestDTO.builder()
                .siteId(siteId)
                .siteName(siteInfo.getSiteName())
                .url(siteInfo.getUrl())
                .comment(siteInfo.getComment())
                .build();
    }
}
