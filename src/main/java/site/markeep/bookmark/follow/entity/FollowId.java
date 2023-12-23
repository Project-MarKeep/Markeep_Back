package site.markeep.bookmark.follow.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter @Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class FollowId implements Serializable {

    private Long fromId;
    private Long toId;

}