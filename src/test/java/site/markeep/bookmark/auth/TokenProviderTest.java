package site.markeep.bookmark.auth;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Base64;

class TokenProviderTest {

    @Test
    @DisplayName("시크릿키 생성 용도")
    void createKey() {
        //given
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[64];
        random.nextBytes(key);
        String encodedKey = Base64.getEncoder().encodeToString(key);
        //when
        System.out.println("\n\n\n");
        System.out.println("encodedKey = " + encodedKey);
        System.out.println("\n\n\n");

        //then

    }

}