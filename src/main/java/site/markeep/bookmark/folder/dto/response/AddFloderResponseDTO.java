package site.markeep.bookmark.folder.dto.response;

import lombok.*;
import site.markeep.bookmark.tag.entity.Tag;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddFloderResponseDTO {


    private Long id; //폴더 id
    private String title; //폴더 title
    private LocalDateTime createDate; //폴더 생성 시간
    private boolean hideFlag; //폴더 공개 여부
    private String folderImg; //폴더 이미지
    private Long userId; //폴더 생성자
    private List<Tag> tags ;

}
