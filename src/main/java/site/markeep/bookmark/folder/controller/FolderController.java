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
import site.markeep.bookmark.folder.dto.request.AddFolderRequestDTO;
import site.markeep.bookmark.folder.dto.response.FolderListResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.service.FolderService;
import site.markeep.bookmark.util.dto.page.PageDTO;

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

            String uploadedFilePath = null;
            if(folderImg != null) {
                log.info("attached file name: {}", folderImg.getOriginalFilename());
                // 전달받은 프로필 이미지를 먼저 지정된 경로에 저장한 후 DB 저장을 위해 경로를 받아오자.
                uploadedFilePath = folderService.uploadProfileImage(folderImg);
            }
            folderService.addFolder(dto,userInfo.getId(), uploadedFilePath );
            return ResponseEntity.ok().body("정상 등록 되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



    @GetMapping("/all")
//    public ResponseEntity<?> getFolderAllList(@Validated @RequestBody PageDTO dto) {
    public ResponseEntity<?> getFolderAllList(@Validated @RequestBody PageDTO dto,BindingResult result) {
        try {
            FolderListResponseDTO list = folderService.getList(dto);
            return ResponseEntity.ok().body(list);
        } catch (StackOverflowError e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

}