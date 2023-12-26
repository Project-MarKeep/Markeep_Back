package site.markeep.bookmark.follow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import site.markeep.bookmark.follow.entity.Follow;
import site.markeep.bookmark.follow.entity.FollowId;

public interface FollowRepository  extends JpaRepository<Follow, FollowId> {

    int countByid_FromId(Long fromId);

    int countByid_ToId(Long toId);

    int countById_FromIdAndId_ToId(Long fromId, Long toId);

//    @Query(value = "SELECT * FROM follow WHERE from_id = ?1 AND to_id = ?2", nativeQuery = true)
//    Follow findFollowRelationship(Long fromId, Long toId);

}
