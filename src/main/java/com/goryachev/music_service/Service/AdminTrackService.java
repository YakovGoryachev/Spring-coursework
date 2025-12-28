package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;

@Service
public class AdminTrackService {

    private FileStorageService fileStorageService;
    private UserService userService;
    private TrackService trackService;

    public AdminTrackService(){

    }

    @Autowired
    public AdminTrackService(FileStorageService fileStorageService, UserService userService, TrackService trackService) {
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.trackService = trackService;
    }

    public void addTrackWithFiles(TrackDto dto, MultipartFile audioFile, MultipartFile avatarFile, String login) throws AccessDeniedException {
        validateAdmin(login);

        String audioPath = "./uploads/" + fileStorageService.storeAudio(audioFile);
        String avatarPath = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarPath = fileStorageService.storeAvatar(avatarFile, "track");
        }

        dto.setFilePath(audioPath);
        dto.setAvatarPath(avatarPath);

        trackService.createTrack(dto);
    }
    public void updateTrack(){

    }
    public void deleteTrack(){

    }

    private void validateAdmin(String login) throws AccessDeniedException {
        User user = userService.findByLogin(login);

        if (user == null) {
            throw new AccessDeniedException("Пользователь не найден");
        }

        if (!user.isAdmin()) {
            throw new AccessDeniedException(
                    "Требуются права администратора. Ваша роль: пользователь"
            );
        }
    }
}
