package site.markeep.bookmark.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import site.markeep.bookmark.follow.entity.Follow;
import site.markeep.bookmark.follow.entity.FollowId;

public interface FollowRepository  extends JpaRepository<Follow, FollowId> {

    int countByid_FromId(Long fromId);

    int countByid_ToId(Long toId);
}
