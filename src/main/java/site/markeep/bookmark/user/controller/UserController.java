package site.markeep.bookmark.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import site.markeep.bookmark.user.dto.request.JoinRequestDTO;
import site.markeep.bookmark.user.dto.request.LoginRequestDTO;
import site.markeep.bookmark.user.dto.response.LoginResponseDTO;
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

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto){

        try {
            LoginResponseDTO responseDTO = userService.login(dto);
            return ResponseEntity.ok().body(responseDTO);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.badRequest().body("관리자의 이메일로 문의해주세요!");
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
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.warn("기타 예외가 발생했습니다.");
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> check(String email) {
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
    @PutMapping
    public ResponseEntity<?> passwordAuth(String email) {

        if(email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("");
        }
        // 400 오류
        if(!userService.isDuplicate(email)) {
            return ResponseEntity.badRequest()
                    .body("미가입 이메일 입니다.");
        }
        //인증번호 반환 : - 200 ok
        return ResponseEntity.ok().body(mailService.sendMail(email));
    }

    @GetMapping("/naver-login")
    public ResponseEntity<?> naverLogin(String code){
        log.info("api/auth/naverLogin - GET! -code:{}", code);
        LoginResponseDTO responseDTO = userService.naverLogin(code);

        return ResponseEntity.ok().body(responseDTO);

    }


}
