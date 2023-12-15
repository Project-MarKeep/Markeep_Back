package site.markeep.bookmark.folder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.dto.request.AddFolderRequestDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.tag.entity.Tag;
import site.markeep.bookmark.tag.repository.TagRepository;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ROLE_USER')")
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;


    @Value("${upload.path.folder}")
    private String uploadRootPath;



    public List<FolderResponseDTO> retrieve(Long userId) {
        User user = getUser(userId);
        List<Folder> folderList = user.getFolders();
        log.warn("user - {}",user);
//        List<Folder> folderList = folderRepository.findAllByUser(user);


        List<FolderResponseDTO> dtoList = folderList.stream()
                .map(folder -> new FolderResponseDTO(folder))
                .collect(Collectors.toList());

        return dtoList;
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원정보가 없습니다.")
        );
    }



    public void addFolder(final AddFolderRequestDTO dto, final  Long id, final  String uploadedFilePath)  throws Exception  {
        // 1. 가입 여부 확인

        User user = userRepository
                .findById(id)
                .orElseThrow(
                        () -> new RuntimeException("가입된 회원이 아닙니다! 회원 가입을 진행해주세요.")
                );

        Folder folder = folderRepository.save(dto.toEntity(dto,user,uploadedFilePath));//혹시 나중에 쓸까 싶어 일단 정보를 담는다

        List<Tag> tagList = new ArrayList<>();
        for (String tag : dto.getTagName()) {
            if (tag == null) break;
            Tag savedTag = tagRepository.save(Tag.builder()
                    .folder(folder)
                    .tagName(tag)
                    .build());
            tagList.add(savedTag); //저장된 태그를 리스트에 추가. 혹시 나중에 쓸까 싶어 일단 정보를 담는다

        }
        log.info("service 여기는 들어 왔다???????????????" , tagList.get(0));

//        return AddFloderResponseDTO.builder()
//                .folderImg(folder.getFolderImg())
//                .title(folder.getTitle())
//                .tags(tagList)
//                .createDate(folder.getCreateDate())
//                .userId(id)
//                .hideFlag(folder.isHideFlag())
//                .build();
    }

    public String uploadProfileImage(MultipartFile folderImg) throws IOException {

        // 루트 디렉토리가 실존하는 지 확인 후 존재하지 않으면 생성.
        File rootDir = new File(uploadRootPath);
        if(!rootDir.exists()) rootDir.mkdirs();

        // 파일명을 유니크하게 변경 (이름 충돌 가능성을 대비)
        // UUID와 원본파일명을 혼합. -> 규칙은 없어요.
        String uniqueFileName
                = UUID.randomUUID() + "_" + folderImg.getOriginalFilename();

        // 파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        folderImg.transferTo(uploadFile);

        return uniqueFileName;
    }

}
