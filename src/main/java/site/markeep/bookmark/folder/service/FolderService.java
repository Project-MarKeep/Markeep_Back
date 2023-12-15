package site.markeep.bookmark.folder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.markeep.bookmark.folder.dto.request.AddFolderRequestDTO;
import site.markeep.bookmark.folder.dto.response.FolderAllResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderListResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.tag.entity.Tag;
import site.markeep.bookmark.tag.repository.TagRepository;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRepository;
import site.markeep.bookmark.util.dto.page.PageDTO;
import site.markeep.bookmark.util.dto.page.PageResponseDTO;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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


    //폴더 전체 목록 조회
//    public List<FolderResponseDTO> getList(PageDTO dto) {
    public FolderListResponseDTO getList(PageDTO dto) {
        //Pageable객체 생성
        Pageable pageable = PageRequest.of(
                dto.getPage() - 1,
                dto.getSize(),
                Sort.by("createDate").descending()
        );
        //데이터 베이스 에서 폴더 목록 조회
        Page<Folder> folderPage = folderRepository.findAll(pageable);
        log.info("TOTAL PAGE 정보  getTotalPages !!!" +  folderPage.getTotalPages());
        log.info("TOTAL FOLDER 정보 getTotalElements !!!" +  folderPage.getTotalElements());
        //게시물 정보만 조회
        List<Folder> folderList = folderPage.getContent();

        // log.info("폴더 서비스 이건 뭐지 !!!!!!! {}", folderList );
        // FolderAllResponseDTO 리스트 생성
        List<FolderAllResponseDTO> responseDTOList = new ArrayList<>();

        for (Folder folder : folderList) {
            // 각 Folder에 대한 Tag 목록 추출
//            List<Tag> tags = folder.getTags();

            // FolderAllResponseDTO 객체 생성 및 리스트에 추가
//            FolderAllResponseDTO responseDTO = new FolderAllResponseDTO(folder, tags);
            FolderAllResponseDTO responseDTO = new FolderAllResponseDTO(folder);
            responseDTOList.add(responseDTO);
        }

        return FolderListResponseDTO.builder()
                .count(folderList.size()) //총 게시물 수가 아니라 조회된 게시물의 개수
                .pageInfo(new PageResponseDTO(folderPage)) //페이지 정보가 담긴 객체를  dto 에게 전달해서 그쪽에서 처리하게 함
                .folders(responseDTOList)
                .build();

    }

}

