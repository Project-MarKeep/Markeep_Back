package site.markeep.bookmark.user.dto.response;

import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Getter @Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDTO {
    private String nickname;
    private String email;
    private int followerCount;
    private int followingCount;
    private HttpHeaders headers;
    private byte[] fileData;


}
