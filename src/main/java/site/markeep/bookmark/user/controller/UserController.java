package site.markeep.bookmark.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.markeep.bookmark.auth.TokenProvider;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.user.dto.request.GoogleLoginRequestDTO;
import site.markeep.bookmark.user.dto.request.JoinRequestDTO;
import site.markeep.bookmark.user.dto.request.LoginRequestDTO;
import site.markeep.bookmark.user.dto.request.PasswordUpdateRequestDTO;
import site.markeep.bookmark.user.dto.response.LoginResponseDTO;
import site.markeep.bookmark.user.dto.response.ProfileResponseDTO;
import site.markeep.bookmark.user.repository.UserRepository;
import site.markeep.bookmark.user.service.UserService;
import site.markeep.bookmark.util.MailService;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user")
@CrossOrigin
public class UserController {


    private final UserService userService;
    private final MailService mailService;
    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto){

        try {
            LoginResponseDTO responseDTO = userService.login(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/join")
    public ResponseEntity<?> join(
            @Validated @RequestBody JoinRequestDTO dto,
            BindingResult result
    ) {
        log.info("/user/join POST! ");
        log.info("JoinRequestDTO: {}", dto);

        if(result.hasErrors()){
            log.warn(result.toString());
            return ResponseEntity.badRequest().body(result.getFieldError());
        }

        try {
            userService.join(dto);
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            log.warn("기타 예외가 발생했습니다.");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/join")
    public ResponseEntity<?> checkingDuplicateEmailInDBBysendingVerifycationCodeToMail(String email) {
        //이메일을 입력하지 않은 경우 빈 문자열 반환-400 오류
        if(email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("");
        }
        log.info("{} 중복?? - {}", email, userService.isDuplicate(email));
        // 400 오류
        if(userService.isDuplicate(email)) {
            return ResponseEntity.badRequest()
                    .body("이미 가입된 이메일 입니다.");
        }
        //인증번호 반환 : - 200 ok
        return ResponseEntity.ok().body(mailService.sendMail(email));
    }

    //password 재 설정시 인증번호 전송
    @PutMapping("/password")
    public ResponseEntity<?> authorizingByUsersEmailBeforeUpdatePassword(String email) {

        if(email.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        // 400 오류
        if(!userService.isDuplicate(email)) {
            return ResponseEntity.badRequest()
                    .body("미가입 이메일 입니다.");
        }
        //인증번호 반환 : - 200 ok
        return ResponseEntity.ok().body(mailService.sendMail(email));
    }

    @PatchMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateRequestDTO dto){
        userService.updatePassword(dto);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleSignIn(@RequestBody GoogleLoginRequestDTO dto) {
        try {
            LoginResponseDTO responseDTO = userService.googleLogin(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @GetMapping("/naver-login")
    public ResponseEntity<?> naverLogin(String code){
        log.info("api/auth/naverLogin - GET! -code:{}", code);

        try {
            LoginResponseDTO responseDTO = userService.naverLogin(code);
            return ResponseEntity.ok().body(responseDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    //프로필 사진 + 닉네임 + 팔로잉/팔로워 수 + 이메일 값 조회해오는 요청
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal TokenUserInfo userInfo){

        try {
            ProfileResponseDTO profile = userService.getProfile(userInfo.getId());
            return ResponseEntity.ok().body(profile);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


}
