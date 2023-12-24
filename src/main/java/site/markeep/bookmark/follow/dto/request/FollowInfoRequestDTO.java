package site.markeep.bookmark.follow.dto.request;

import lombok.*;
import site.markeep.bookmark.auth.TokenUserInfo;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowInfoRequestDTO {

    private TokenUserInfo userInfo;

    private Long toId;

}
