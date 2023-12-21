package site.markeep.bookmark.user.dto.response;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private String profileImage;
    private String nickname;
    private String email;
    private int followerCount;
    private int followingCount;
}
