package site.markeep.bookmark.follow.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.entity.QFolder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.follow.entity.Follow;
import site.markeep.bookmark.follow.entity.FollowId;
import site.markeep.bookmark.follow.repository.FollowRepository;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRepository;

import java.util.Optional;

import static site.markeep.bookmark.folder.entity.QFolder.folder;
import static site.markeep.bookmark.follow.entity.QFollow.follow;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;
    private final FolderRepository folderRepository;
    private final JPAQueryFactory queryFactory;

    public void follow(TokenUserInfo userInfo, Long toId) {

        // 팔로 정보 저장
        Follow followSaved = followRepository.save(
                Follow.builder()
                        .id(
                                FollowId.builder()
                                        .fromId(userInfo.getId())
                                        .toId(toId)
                                        .build()
                        )
                        .build()
        );
    }

    /*
        1. 긍까 먼저 userInfo.getId()랑 toId가 followRepository.findbyId()했 -> 안해
        2. 생각해보니 queryFactory로 셀렉하면댐 해오겟음
        3. 복합키ㅗ
        4. 쿼리메소드사용
     */
    public void deleteFollow(TokenUserInfo userInfo, Long toId) {

        // 팔로 정보 삭제
        queryFactory.delete(follow)
                .where(follow.id.fromId.eq(userInfo.getId()).and(follow.id.toId.eq(toId)))
                .execute();
    }
}
