package site.markeep.bookmark.pinn.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.user.entity.User;

import javax.persistence.*;

@Getter
@Setter
@ToString(exclude = "folder")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Pin {

    @Id
    @Column(name = "pin_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "folder_id")
    @JsonBackReference
    private Folder folder;

    @Column(nullable = false)
    private Long newFolderId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "new_folder_id")
//    @JsonBackReference
//    private Folder newFolder;


//    @ManyToOne
//    @JoinColumn(name = "user_id")
//    @JsonBackReference
//    private User user;

//    @PreRemove
//    private void preRemove() {
//        if (newFolder != null) {
//            newFolder.getPins().remove(this);
//        }
//    }


}
