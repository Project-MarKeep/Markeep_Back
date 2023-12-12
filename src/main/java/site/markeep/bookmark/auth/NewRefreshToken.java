package site.markeep.bookmark.auth;

import lombok.*;
import site.markeep.bookmark.user.entity.User;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class NewRefreshToken {

    /*
        - refreshToken
        자동 로그인 한 사람이 죽은 accessToken을 들고 요청함
        refreshToken이 있다면 새로운 토큰을 발급해줄거야
        이거는 /user/login에서 하지못해
        그럼 새로운 accessToken을 요구하는 요청을 받는 메서드를 작성해야함
        근데? 이거를 생각해보면? /user에서,, 받아도 되는건가?
        따로 api전용 컨트롤러를 만들어야하나?
        ㄴㄴ /user에서 받아도 될 것 같음

        - 그럼 /user/newtoken 에서의 내용이 뭐냐
        먼저 받은 토큰에서
     */

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String refreshToken;

    // 액세스토큰 재요청 횟수 제한 용도
    private int reissueCount = 0;


    // 기본 저장용 / 기본 생성자
    public NewRefreshToken(User user, String refreshToken) {
        this.user = user;
        this.refreshToken = refreshToken;
    }

    // 새로운 토큰 발급 받으면 그걸로 저장해주는 메서드
    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    // DB에 저장한 refreshToken과 유저가 보낸 refreshToken 비교해서
    // 기간이 만료 된건지 안된건지 비교해주는 메서드
    public boolean validateRefreshToken(String refreshToken){
        return this.refreshToken.equals(refreshToken);
    }

    public void countRequest(){
        reissueCount++;
    }


}
