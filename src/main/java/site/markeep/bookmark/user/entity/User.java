package site.markeep.bookmark.user.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import site.markeep.bookmark.folder.entity.Folder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
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

    @OneToMany(mappedBy = "user")
    @Builder.Default
    @JsonManagedReference
    private List<Folder> folders = new ArrayList<>();

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}