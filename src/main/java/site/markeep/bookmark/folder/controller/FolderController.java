package site.markeep.bookmark.folder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.service.FolderService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/folders")
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/my")
    public ResponseEntity<?> getFolderList(@AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("/folders/my - GET 요청! {},", userInfo);
        List<FolderResponseDTO> folderList = folderService.retrieve(userInfo.getId());
        return ResponseEntity.ok().body(folderList);
    }
}
