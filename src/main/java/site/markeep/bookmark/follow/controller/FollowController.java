package site.markeep.bookmark.follow.controller;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.follow.entity.Follow;
import site.markeep.bookmark.follow.service.FollowService;

import static site.markeep.bookmark.follow.entity.QFollow.follow;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/follow")
@CrossOrigin
public class FollowController {

    private final FollowService followService;
    private final JPAQueryFactory queryFactory;

    /*
        @AuthenticationPrincipal
        :
     */

    @PostMapping
    public ResponseEntity<?> follow(
            @AuthenticationPrincipal TokenUserInfo userInfo,
            Long toId
    ){

//        log.warn("[CONTROLLER] - follow");
//        log.warn("일단 요청 들어오는지 확인해볼개ㅐㅐ userInfo : {}",userInfo);
//        log.warn("일단 요청 들어오는지 확인해볼개ㅐㅐ toId : {}", toId);

//        log.warn("tokenUserInfo의 id 값이 로그인 한 유저의 id 값아님?>?? : {}", id);

        Follow followResult = queryFactory.selectFrom(follow)
                .where(follow.id.fromId.eq(userInfo.getId()).and(follow.id.toId.eq(toId)))
                .fetchFirst();
        log.warn("일단 select되는지 확인: {}", followResult);
//        Follow followRelationship = followRepository.findFollowRelationship(userInfo.getId(), toId);
//        log.warn("쿼리 메서드 결과 : {}", followRelationship);

        // 로그인 한 유저가 할 수 있는 행동이긴 하지만
        // 로그인 한 유저의 id값 = 똑같은 id값 -> 막기
        if(toId.equals(userInfo.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            // 1. follow / unfollow 구별하기 -> flag 느낌
            if(followResult == null){
                followService.follow(userInfo, toId);
                return ResponseEntity.status(HttpStatus.OK).build();
            } else {
                followService.deleteFollow(userInfo, toId);
                return ResponseEntity.status(HttpStatus.OK).build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

}
