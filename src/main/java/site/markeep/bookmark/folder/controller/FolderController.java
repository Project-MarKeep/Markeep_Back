package site.markeep.bookmark.folder.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.dto.AddFloderResponseDTO;
import site.markeep.bookmark.folder.dto.AddFolderRequestDTO;
import site.markeep.bookmark.folder.service.FolderService;


@RestController
@RequestMapping("/folders")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin
public class FolderController {

    private final FolderService folderService;


    @GetMapping("/my")
    public ResponseEntity<?> getFolderList(){

        return null;
    }

    //폴더 추가 요청
    @PostMapping("/my" )
    public  ResponseEntity<?> addFolder(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestPart AddFolderRequestDTO dto,
            @RequestPart(value = "folderImage", required = false) MultipartFile folderImg,
            BindingResult result
    ){
        log.info("/folders/my/ POST userInfo ! " +  userInfo);
        log.info("/folders/my/ POST dto ! " +  dto);
        log.info("/folders/my/ POST folderImg ! " +  folderImg);

        if(result.hasErrors()){
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }
        if(userInfo == null || userInfo.getId() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("");
        }
        try {
//            folderService.addFolder(dto,userInfo.getId());
//            AddFloderResponseDTO addFloderResponseDTO = folderService.addFolder(dto, 1L);//test 를 위해 임시

            String uploadedFilePath = null;
            if(folderImg != null) {
                log.info("attached file name: {}", folderImg.getOriginalFilename());
                // 전달받은 프로필 이미지를 먼저 지정된 경로에 저장한 후 DB 저장을 위해 경로를 받아오자.
                uploadedFilePath = folderService.uploadProfileImage(folderImg);
            }

            folderService.addFolder(dto, 1L, uploadedFilePath );//test 를 위해 임시
            return ResponseEntity.ok().body("정상 등록 되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}