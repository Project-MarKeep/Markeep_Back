package site.markeep.bookmark.folder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.dto.request.FolderUpdateRequestDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.service.FolderService;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/folders")
@CrossOrigin
public class FolderController {

    private final FolderService folderService;

    @GetMapping("/my")
    public ResponseEntity<?> getList(@AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("/folders/my - GET 요청! {},", userInfo);
        List<FolderResponseDTO> folderList = folderService.retrieve(userInfo.getId());
        return ResponseEntity.ok().body(folderList);
    }

    @PatchMapping("/my")
    public ResponseEntity<?> update(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @RequestBody FolderUpdateRequestDTO dto
    ){
        log.info("/folders/my - PATCH 요청! - {}", dto);
        dto.setUserId(userInfo.getId());
        folderService.update(dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/my/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long folderId){
        folderService.delete(folderId);
        return ResponseEntity.ok().build();
    }
}
