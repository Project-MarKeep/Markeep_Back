package site.markeep.bookmark.site.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.site.dto.AddSiteRequestDTO;
import site.markeep.bookmark.site.entity.Site;
import site.markeep.bookmark.site.repository.SiteRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;
    private final FolderRepository folderRepository;

    public void addSite(Long id, Long folderId, AddSiteRequestDTO dto) {
        Folder foundFolder = folderRepository.findById(folderId).orElseThrow(
                () -> new RuntimeException("없는 폴더입니다. 먼저 폴더를 생성해주세요!")
        );

        siteRepository.save(Site.builder()
                        .siteName(dto.getSiteName())
                        .url(dto.getUrl())
                        .comment(dto.getComment())
                        .folder(foundFolder)
                        .build());
    }
}
