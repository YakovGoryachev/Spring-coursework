package com.goryachev.music_service.Utility;

import com.mpatric.mp3agic.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AudioUtils {

    /**
     * Извлекает длительность MP3-файла в секундах из MultipartFile
     */
    public static int getMp3DurationSeconds(MultipartFile file)
            throws IOException, UnsupportedTagException, InvalidDataException {

        // Создаём временный файл
        Path tempFile = Files.createTempFile("mp3_", ".mp3");

        try {
            // Копируем MultipartFile во временный файл
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            // Открываем Mp3File по пути
            Mp3File mp3 = new Mp3File(tempFile.toString());
            long durationMs = mp3.getLengthInMilliseconds();
            return (int) Math.round(durationMs / 1000.0);

        } finally {
            // Удаляем временный файл
            Files.deleteIfExists(tempFile);
        }
    }

    /**
     * Форматирует длительность в формат MM:SS
     */
    public static String formatDuration(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
}