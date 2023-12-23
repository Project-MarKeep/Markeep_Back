package site.markeep.bookmark.follow.entity;

import lombok.*;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "follow")
public class Follow {

    @EmbeddedId
    private FollowId id; // 복합 키 필드

}