package site.markeep.bookmark.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import site.markeep.bookmark.follow.entity.Follow;
import site.markeep.bookmark.follow.entity.FollowId;

public interface FollowRepository  extends JpaRepository<Follow, FollowId> {

    int countByid_FromId(Long fromId);

    int countByid_ToId(Long toId);

    @Query("SELECT COUNT(*) FROM Follow f WHERE f.id.fromId = :fromId AND f.id.toId = :toId")
    int countById_FromIdAndId_ToId(@Param("fromId") Long fromId, @Param("toId") Long toId);

}
