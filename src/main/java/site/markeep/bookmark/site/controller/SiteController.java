package site.markeep.bookmark.site.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.site.dto.request.AddSiteRequestDTO;
import site.markeep.bookmark.site.dto.request.SingleSiteInfoRequestDTO;
import site.markeep.bookmark.site.dto.request.SiteDeleteRequestDTO;
import site.markeep.bookmark.site.dto.request.UpdateSiteInfoRequestDTO;
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
        try {
            List<?> sites = siteService.addSite(dto);
            return ResponseEntity.ok().body(sites);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

//        log.warn("사이트가 등록되는게 맞나 볼게: {}", sites);

    }

    // 사이트 목록 조회
    @GetMapping
    public ResponseEntity<?> getSiteList(@RequestParam Long folderId){
        log.warn("GET - getSiteList 요청 들어옴!");
        try {
            List<Site> siteList = siteService.getSiteList(folderId);
            return ResponseEntity.ok().body(siteList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 사이트 등록 정보 수정 (기존의 값 입력되어 있어야 함)
    @PatchMapping
    public ResponseEntity<?> updateRegistSiteInfo(@RequestBody UpdateSiteInfoRequestDTO dto){
//        log.warn("[CONTROLLER] updateRegistSiteInfo메서드에 들어온건 마자여???????");
        try {
            siteService.updateRegistSiteInfo(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    // 사이트 단일 조회
    @GetMapping("/single")
    public ResponseEntity<?> getSingleSiteData(Long siteId){
        log.warn("GET - /site/single : {}", siteId);
        try {
            SingleSiteInfoRequestDTO singleSiteData = siteService.getSingleSiteData(siteId);
            return ResponseEntity.ok().body(singleSiteData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 사이트 삭제
    @DeleteMapping
    public  ResponseEntity<?> deleteSite(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody SiteDeleteRequestDTO dto
    ) {

        try {
            siteService.deleteSite(userInfo.getId(), dto);
            return ResponseEntity.ok().body("사이트가 정상적으로 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
