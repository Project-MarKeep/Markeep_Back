package site.markeep.bookmark.tag.entity;


import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import site.markeep.bookmark.folder.entity.Folder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table
public class Tag {

    @Id
    @Column(name = "tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tagName;

    @CreationTimestamp
    private LocalDateTime regdate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

}
