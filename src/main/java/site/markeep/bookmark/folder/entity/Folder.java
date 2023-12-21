package site.markeep.bookmark.folder.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import site.markeep.bookmark.folder.dto.request.FolderUpdateRequestDTO;
import site.markeep.bookmark.pinn.entity.Pin;
import site.markeep.bookmark.site.entity.Site;
import site.markeep.bookmark.tag.entity.Tag;
import site.markeep.bookmark.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString(exclude = {"tags", "user", "sites"})
@EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folder_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @CreationTimestamp
    private LocalDateTime createDate;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean hideFlag;

    @Column(nullable = false)
    private Long creator;

    private String folderImg;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "folder",orphanRemoval = true)
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    @OneToMany(mappedBy = "folder",orphanRemoval = true)
    @Builder.Default
    private List<Pin> pins = new ArrayList<>();

    @OneToMany(mappedBy = "folder",orphanRemoval = true)
    @Builder.Default
    private List<Site> sites = new ArrayList<>();

    public  void  addTag(Tag tag) {
        this.tags.add(tag);//매개값으로 전달받은  Tag객체를 리스트에 추가

        //매개값으로 전달된 Tag객체가 가지고 있는 Folder가
        //이 메서드를 부를는 Folder객체와 주소값이 서로 다르다면 데이터 불일치가 발생하기 때문에
        //Tagdml Folder 의 값도 이 객체로 변경
        if (this != tag.getFolder()) {
            tag.setFolder(this);
        }
    }
    
    public void update(FolderUpdateRequestDTO dto){
        this.creator = dto.getUserId();
        this.title = dto.getTitle();
        this.tags = dto.getTags();
        this.hideFlag = dto.isHideFlag();
    }
}