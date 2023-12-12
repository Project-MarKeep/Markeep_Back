package site.markeep.bookmark.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MailService {

    private final JavaMailSender javaMailSender;


    private static final String senderEmail= "hyejink355@gmail.com";

    private int verificationCode;

    public void generateVerificationCode() {
        Random random = new Random();
        verificationCode = random.nextInt(90000) + 100000;
    }


    public MimeMessage  CreateMail(String email){

        generateVerificationCode();
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("BOOKMARK 이메일 인증");
            String body = "요청하신 인증 번호입니다: " + verificationCode + "\n감사합니다.";
            message.setText(body, "UTF-8");
        } catch (MessagingException e) {
            log.error("Error creating email message", e);
        }

        return message;
    }

    public int sendMail(String email){
//        log.info("MailService 에 들어 오다 email: {}", email);
        MimeMessage message = CreateMail(email);
        log.info("Email sent successfully message : {}", message);
        javaMailSender.send(message);
        log.info("Email sent successfully  send : {}", message);

        return verificationCode;
    }
}
