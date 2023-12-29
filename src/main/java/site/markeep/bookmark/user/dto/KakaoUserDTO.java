package site.markeep.bookmark.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import site.markeep.bookmark.user.entity.User;

import java.time.LocalDateTime;

@Setter @Getter
@ToString
public class KakaoUserDTO {

    private long id;

    @JsonProperty("connected_at")
    private LocalDateTime connectedAt;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Setter @Getter @ToString
    public static class KakaoAccount {

        private String email;
        private Profile profile;

        @Getter @Setter @ToString
        public static class Profile {
            private String nickname;

        }



    }

    public User toEntity(String refreshToken){
        return User.builder()
                .email(this.kakaoAccount.email)
                .nickname(this.kakaoAccount.profile.nickname)
                .password("password!")
                .refreshToken(refreshToken)
                .build();
    }
}