package site.markeep.bookmark.follow.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
@Builder
public class FollowId implements Serializable {

    private Long fromId;
    private Long toId;

}
