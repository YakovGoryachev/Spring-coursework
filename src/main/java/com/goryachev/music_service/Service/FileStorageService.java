package com.goryachev.music_service.Service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;

@Service
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.audio-dir}")
    private String audioDir;

    @Value("${file.avatars-dir}")
    private String avatarsDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(audioDir));
            Files.createDirectories(Paths.get(avatarsDir));
            System.out.println("Директории созданы: " + audioDir + ", " + avatarsDir);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директории для загрузки", e);
        }
    }

    /**
     * Сохраняет аудиофайл
     * @param file аудиофайл
     * @return путь относительно корня (например: "audio/2024/01/track_123.mp3")
     */
    public String storeAudio(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String filename = "track_" + System.currentTimeMillis() + "." + fileExtension;


        LocalDate now = LocalDate.now();
        String yearMonth = now.getYear() + "/" + String.format("%02d", now.getMonthValue());
        String relativePath = "audio/" + yearMonth + "/" + filename;

        Path destination = Paths.get(uploadDir).resolve(relativePath);

        try {
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить файл: " + filename, e);
        }
    }

    /**
     * Сохраняет обложку
     * @param file изображение
     * @param type тип (track, artist, album)
     * @return путь к обложке
     */
    public String storeAvatar(MultipartFile file, String type) {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String filename = type + "_" + System.currentTimeMillis() + "." + fileExtension;

        String relativePath = "avatars/" + type + "s/" + filename;
        Path destination = Paths.get(uploadDir).resolve(relativePath);

        try {
            Files.createDirectories(destination.getParent());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            createThumbnail(destination, filename);

            return relativePath;
        } catch (IOException e) {
            throw new RuntimeException("Не удалось сохранить обложку", e);
        }
    }

    public Resource loadFile(String filepath) {
        try {
            Path file = Paths.get(uploadDir).resolve(filepath);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Файл не найден: " + filepath);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Ошибка загрузки файла: " + filepath, e);
        }
    }

    public void deleteFile(String filepath) {
        try {
            Path file = Paths.get(uploadDir).resolve(filepath);
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось удалить файл: " + filepath, e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "mp3";
        }
        return filename.substring(filename.lastIndexOf(".") + 1);
    }

    private void createThumbnail(Path source, String filename) {

    }
}
