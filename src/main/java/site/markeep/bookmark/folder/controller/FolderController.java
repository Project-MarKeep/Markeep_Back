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
import site.markeep.bookmark.folder.dto.request.FolderUpdateRequestDTO;
import site.markeep.bookmark.folder.dto.response.FolderListResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.service.FolderService;
import site.markeep.bookmark.util.dto.page.PageDTO;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/folders")
@CrossOrigin
public class FolderController {

    private final FolderService folderService;

    // 마이페이지 입장 시, 기본폴더부터 모든 폴더 불러와주는 메서드
    @GetMapping("/my")
    public ResponseEntity<?> getList(@AuthenticationPrincipal TokenUserInfo userInfo) {
        log.info("/folders/my - GET 요청! {},", userInfo);
        List<FolderResponseDTO> folderList = folderService.retrieve(userInfo.getId());
        return ResponseEntity.ok().body(folderList);
    }

    // 폴더 정보 업데이트 시켜주는 메서드
    @PatchMapping("/my")
    public ResponseEntity<?> update(
            @RequestBody FolderUpdateRequestDTO dto
    ){
        log.info("/folders/my - PATCH 요청! - {}", dto);
        List<FolderResponseDTO> updatedList = folderService.update(dto);

        return ResponseEntity.ok().body(updatedList);
    }

    //폴더 삭제 요청
    @DeleteMapping("/my/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long folderId) {
        folderService.delete(folderId);
        return ResponseEntity.ok().build();
    }
    
    /*****************************************************
      .폴더 추가 요청
      .생성 ENTITY : Folder , Tag , Folder image
      .return : 정상 등록 유무 message
    *****************************************************/
    @PostMapping("/my")
    public  ResponseEntity<?> addFolder(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            @Validated @RequestPart AddFolderRequestDTO dto,
            @RequestPart(value = "folderImage", required = false) MultipartFile folderImg,
            BindingResult result
    ){

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
                // 전달받은 프로필 이미지를 먼저 지정된 경로에 저장한 후 DB 저장을 위해 경로를 받아오자.
                uploadedFilePath = folderService.uploadFolderImage(folderImg);
            }
            folderService.addFolder(dto,userInfo.getId(), uploadedFilePath );
            return ResponseEntity.ok().body("정상 등록 되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /*****************************************************
     . 모든 폴더 LIST 를 조회 (SORT 순서: PIN COUNT DESC , CREATEDATE DESC)
     . paging 처리
     . return 조회건수 + page 객체 + Floder / Tag / Pin
     *****************************************************/
     @GetMapping("/all")
    public ResponseEntity<?> getFolderAllList(
//            @AuthenticationPrincipal TokenUserInfo userInfo,
             PageDTO dto,
             String keyWord ) {

         // 키워드에 spaces 가 있으면 쪼갠다


        try {
            FolderListResponseDTO list = folderService.getList(dto,keyWord);
            return ResponseEntity.ok().body(list);
        } catch (StackOverflowError e){
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

    /*****************************************************
     . 커뮤니티 화면에서 폴더의 pin hover 누르면, 해당 폴더가 마이페이지에 생긴다.
     . 1.userInfo 로 유저 check -> 없으면 "미가입 user 입니다. MSG"
     . 2.(service) 해당 폴더가 있는지 check -> 없으면  "잘못된 폴더 번호 입니다. MSG"
     . 3.(service) Folder , Site , Pin, FolderImg 생성
     . 4.return Folder + Site , 정상 생성 유무
     *****************************************************/
    @PostMapping("/pin" )
    public  ResponseEntity<?> addFolderPin(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            int folderId
    ) throws Exception {

        if(userInfo == null || userInfo.getId() == null) {
            return ResponseEntity
                    .badRequest()
                    .body("미가입 회원입니다.");
        }
        try {
            FolderResponseDTO folderResponseDTO =  folderService.addFolderPin(userInfo.getId(), Long.valueOf(folderId));
            return  ResponseEntity.ok().body(folderResponseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }

}
