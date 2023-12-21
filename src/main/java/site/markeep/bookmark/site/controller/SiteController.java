package site.markeep.bookmark.site.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.site.dto.request.AddSiteRequestDTO;
import site.markeep.bookmark.site.entity.Site;
import site.markeep.bookmark.site.repository.SiteRepository;
import site.markeep.bookmark.site.service.SiteService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/site")
@CrossOrigin
public class SiteController {

    private final SiteService siteService;
    private final SiteRepository siteRepository;

    @PostMapping
    public ResponseEntity<?> addSite(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody AddSiteRequestDTO dto
            ){
        log.info("/site - POST 요청! {}", dto);
        List<?> sites = siteService.addSite(dto);

        log.warn("사이트가 등록되는게 맞나 볼게: {}", sites);

        return ResponseEntity.ok().body("사이트가 정상적으로 등록되었습니다.");
    }

    // 폴더 하나 선택하면 폴더 안에 있는 사이트들 다 가져오는 메서드
    @GetMapping
    public ResponseEntity<?> getSiteList(@RequestParam Long folderId){
        log.warn("GET - getSiteList 요청 들어옴!");
        List<Site> siteList = siteService.getSiteList(folderId);
        return ResponseEntity.ok().body(siteList);
//        log.warn("GET - getSiteList 요청 들어옴!");
//        List<Site> siteList = siteService.getSiteList(folderId);
//        return ResponseEntity.ok().body(siteList);
    }
}
