package site.markeep.bookmark.folder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import site.markeep.bookmark.folder.dto.request.AddFolderRequestDTO;
import site.markeep.bookmark.folder.dto.response.FolderAllResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderListResponseDTO;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.pinn.entity.Pin;
import site.markeep.bookmark.pinn.repository.PinRepository;
import site.markeep.bookmark.site.entity.Site;
import site.markeep.bookmark.site.repository.SiteRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
//@PreAuthorize("hasRole('ROLE_USER')")
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final SiteRepository siteRepository;
    private final PinRepository pinRepository;

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

//        User user = userRepository
//                .findById(id)
//                .orElseThrow(
//                        () -> new RuntimeException("가입된 회원이 아닙니다! 회원 가입을 진행해주세요.")
//                );
        User user = getUser(id);

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

    public String uploadFolderImage(MultipartFile folderImg) throws IOException {

        // 루트 디렉토리가 실존하는 지 확인 후 존재하지 않으면 생성.
        // 배포시 막는다
        File rootDir = new File(uploadRootPath);
        if(!rootDir.exists()) rootDir.mkdirs();

        // 파일명을 유니크하게 변경 (이름 충돌 가능성을 대비)
        // UUID와 원본파일명을 혼합. -> 규칙은 없어요.
        String uniqueFileName
                = UUID.randomUUID() + "_" + folderImg.getOriginalFilename();

        // 파일을 저장
        //배포시 막는다
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        folderImg.transferTo(uploadFile);

        // 배포시 막는다
        return uniqueFileName;
        // 배포시 푼다
//        return s3Service.uploadToS3Bucket(folderImg.getBytes(), uniqueFileName);

    }


    //폴더 전체 목록 조회
    public FolderListResponseDTO getList(PageDTO dto , String keyWord ) {
        Pageable pageable = PageRequest.of(dto.getPage() - 1, dto.getSize());
//        log.info("getList folder keyWord sql문 !!!!!! : {}",makeKeyWords(keyWord));
        String[] keyWords = keyWord.split("\\s+");
//        Page<Folder> folderPage = folderRepository.findAllOrderByPinCountKeyWords(pageable, makeKeyWords(keyWord));
        log.info("getList folder keyWord sql문 keyWord !!!!!! : {}",keyWord);
        Page<Folder> folderPage = folderRepository.findAllOrderByPinCountKeyWords(pageable, keyWords);

        List<Folder> folders = folderPage.getContent(); // 현재 페이지의 데이터
        long totalElements = folderPage.getTotalElements(); // 전체 데이터의 갯수
        int totalPages = folderPage.getTotalPages(); // 전체 페이지 수

        // log.info("폴더 서비스 이건 뭐지 !!!!!!! {}", folderList );
        // FolderAllResponseDTO 리스트 생성
        List<FolderAllResponseDTO> responseDTOList = new ArrayList<>();
        for (Folder folder : folders) {
            // 각 Folder에 대한 Tag 목록 추출
            FolderAllResponseDTO responseDTO = new FolderAllResponseDTO(folder);
            responseDTOList.add(responseDTO);
        }

        return FolderListResponseDTO.builder()
                .count(folders.size()) //총 게시물 수가 아니라 조회된 게시물의 개수
                .pageInfo(new PageResponseDTO(folderPage)) //페이지 정보가 담긴 객체를  dto 에게 전달해서 그쪽에서 처리하게 함
                .folders(responseDTOList)
                .build();

    }

    // 키워드 들로 폴더 정보를 조회한다.
    // sort 순은 핀많이 받은순 + 등록순
    // 여기서 query 문을 완성한다
//    private String makeKeyWords(String keyWord) {
//        if (keyWord == null || keyWord.isEmpty()) {
//            return "";// keyWord가 null이거나 비어있으면 빈 문자열 반환
//        }
//
//        // 공백을 기준으로 분리하여 배열로 저장
//        String[] keyWords = keyWord.split("\\s+");
//        StringBuilder queryBuilder = new StringBuilder("WHERE ");
//        List<String> conditions = new ArrayList<>();
//
//        for (int i = 0; i < keyWords.length; i++) {
//            if (keyWords[i] != null && !keyWords[i].isEmpty() ) {
//                String condition = String.format("LOWER(f.title) LIKE LOWER(CONCAT('%%', :keyWord%d, '%%'))", i);
//                conditions.add(condition);
//            }
//        }
//
//        if (!conditions.isEmpty()) {
//            queryBuilder.append("(");
//            queryBuilder.append(String.join(" AND ", conditions));
//            queryBuilder.append(") ");
//        }
//
//        return makeKeyWords(queryBuilder.toString());
//
//    }


    /*****************************************************
     . 커뮤니티 화면에서 폴더의 pin hover 누르면, 해당 폴더가 마이페이지에 생긴다.
     . 2.(service) 해당 폴더가 있는지 check -> 없으면  "잘못된 폴더 번호 입니다. MSG"
     . 3.(service) Folder , Site , Pin 생성
     . 4.return Folder + Site , 정상 생성 유무
     *****************************************************/

    public FolderResponseDTO addFolderPin(Long userId, Long folderId) throws Exception {
        Folder folder = folderRepository.findById(folderId)
                .orElseThrow(
                () -> new RuntimeException("잘못된 폴더 번호 입니다. 확인해주세요 ")
                );

        User user = getUser(userId);

        //Folder , Site , Pin 생성
        AddFolderRequestDTO dto =  AddFolderRequestDTO.builder()
                .title(folder.getTitle())
                .build();

        //폴더 생성전에 이미지를 먼저 새로운 이름으로 복사 , 생성한다.
        String newFolderName = imageCopy(folder.getFolderImg());
        //폴더 생성
        //고려사항 발생, 폴더 이미지도 같이 복사,생성하는데 이지미id 를 새로 생성해서 저장하는 작업을 따로 해야함(db 생성)


//        Folder folderNew = folderRepository.save(dto.toEntity(dto,user,folder.getFolderImg()));//혹시 나중에 쓸까 싶어 일단 정보를 담는다
        Folder folderNew = folderRepository.save(dto.toEntity(dto,user,newFolderName));//혹시 나중에 쓸까 싶어 일단 정보를 담는다

        //핀 생성
        pinRepository.save(Pin.builder().folder(folder).user(user).build());

        //Site 생성
        List<Site> sites = siteRepository.findByFolderId(folderId);
        for (Site site : sites){
            siteRepository.save(Site.builder()
                    .siteName(site.getSiteName())
                    .url(site.getUrl())
                    .comment(site.getComment())
                    .folder(folderNew)
                    .build());
        }

        return  new FolderResponseDTO(folderNew);
    }

    //기존의 이미지 파일을 복사, 새로운 url 을 부여 한다.
    public String imageCopy(String imgName) throws Exception {
        File rootDir = new File(uploadRootPath);
        if(!rootDir.exists()) rootDir.mkdirs();

        // 파일명을 유니크하게 변경 (이름 충돌 가능성을 대비)
        // UUID와 원본파일명을 혼합. -> 규칙은 없어요.

        String extension = getFileExtension(imgName);

        String uniqueFileName
                = String.valueOf(UUID.randomUUID());

        File source = new File(uploadRootPath + "/" + imgName);
        File dest = new File(uploadRootPath + "/" + uniqueFileName+"."+extension);
        log.info("화일 확장자명!!!!!!! : {}",dest);

        try {
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
       return  uniqueFileName+"."+extension;
//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpget = new HttpGet(imageUrl);
//        CloseableHttpResponse response = httpclient.execute(httpget);
//        try {
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                byte[] bytes = EntityUtils.toByteArray(entity);
//                try (OutputStream os = new FileOutputStream(uniqueFileName)) {
//                    os.write(bytes);
//                }
//            }
//        } finally {
//            response.close();
//        }
//        return  uniqueFileName;
    }

    private static String getFileExtension(String fileName) {
        String extension = "";
        if(fileName.toString().contains(".")) {
            extension = fileName.toString().split("\\.")[1];
        }
        return extension;
    }
}


