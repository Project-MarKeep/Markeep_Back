package site.markeep.bookmark.folder.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import site.markeep.bookmark.auth.TokenUserInfo;
import site.markeep.bookmark.folder.dto.response.FolderResponseDTO;
import site.markeep.bookmark.folder.entity.Folder;
import site.markeep.bookmark.folder.repository.FolderRepository;
import site.markeep.bookmark.user.entity.User;
import site.markeep.bookmark.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FolderService {

    private final FolderRepository folderRepository;
    private final UserRepository userRepository;

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
}
