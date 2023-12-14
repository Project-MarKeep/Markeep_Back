package site.markeep.bookmark.folder.entity;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import site.markeep.bookmark.folder.dto.request.FolderUpdateRequestDTO;
import site.markeep.bookmark.tag.entity.Tag;
import site.markeep.bookmark.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@ToString(exclude = "user") @EqualsAndHashCode
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
    private User user;

    @OneToMany(mappedBy = "folder")
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();

    public void update(FolderUpdateRequestDTO dto){
        this.creator = dto.getUserId();
        this.title = dto.getTitle();
        this.tags = dto.getTags();
        this.hideFlag = dto.isHideFlag();
    }
}