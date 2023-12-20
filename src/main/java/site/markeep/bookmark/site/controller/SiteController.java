package site.markeep.bookmark.site.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.site.dto.AddSiteRequestDTO;
import site.markeep.bookmark.site.service.SiteService;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/site")
@CrossOrigin
public class SiteController {

    private final SiteService siteService;

    @PostMapping
    public ResponseEntity<?> addSite(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            Long folderId,
            AddSiteRequestDTO dto
            ){
        log.info("/site - POST 요청! {}", dto);
        siteService.addSite(userInfo.getId(), folderId, dto);

        return ResponseEntity.ok().body("사이트가 정상적으로 등록되었습니다.");
    }
}
