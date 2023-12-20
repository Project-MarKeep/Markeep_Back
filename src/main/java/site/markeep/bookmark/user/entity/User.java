package site.markeep.bookmark.user.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.follow.entity.Follower;
import site.markeep.bookmark.follow.entity.Following;
import site.markeep.bookmark.pinn.entity.Pin;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString(exclude = "folders") @EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user")
@Entity
@DynamicInsert
public class User  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @CreationTimestamp
    @Column(nullable = false, name = "join_date")
    private LocalDateTime joinDate;

    @Column
    private String profileImage;

    @Column(unique = true)
    private String refreshToken;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean autoLogin;

    @Column(nullable = false, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    // provider에는 google이 들어가게 되고,
    @Column
    private String provider;
    // providerId에는 구글로 로그인 한 유저의 고유 ID가 들어가게 된다.
    @Column
    private String providerId;

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Folder> folders = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Follower> followers = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Following> followings = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<Pin> pins = new ArrayList<>();

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
