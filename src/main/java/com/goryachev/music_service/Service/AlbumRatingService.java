package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.AlbumRatingDto;
import com.goryachev.music_service.Pojo.Album;
import com.goryachev.music_service.Pojo.AlbumRating;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.AlbumRatingRepository;
import com.goryachev.music_service.Repository.AlbumRepository;
import com.goryachev.music_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumRatingService {

    private final AlbumRatingRepository albumRatingRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    @Autowired
    public AlbumRatingService(AlbumRatingRepository albumRatingRepository,
                             UserRepository userRepository,
                             AlbumRepository albumRepository) {
        this.albumRatingRepository = albumRatingRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    public AlbumRating findById(int id) {
        return albumRatingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оценка альбома не найдена"));
    }

    public List<AlbumRating> findByAlbumId(int albumId) {
        return albumRatingRepository.findByAlbum_Id(albumId);
    }

    public List<AlbumRating> findByUserId(int userId) {
        return albumRatingRepository.findByUser_Id(userId);
    }

    public AlbumRating findByUserAndAlbum(int userId, int albumId) {
        return albumRatingRepository.findByUser_IdAndAlbum_Id(userId, albumId);
    }

    public double getAverageRatingForAlbum(int albumId) {
        List<AlbumRating> ratings = findByAlbumId(albumId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(AlbumRating::getValue)
                .average()
                .orElse(0.0);
    }

    public AlbumRating createOrUpdateRating(AlbumRatingDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Album album = albumRepository.findById(dto.getAlbumId())
                .orElseThrow(() -> new RuntimeException("Альбом не найден"));

        AlbumRating existingRating = findByUserAndAlbum(dto.getUserId(), dto.getAlbumId());

        if (existingRating != null) {
            existingRating.setValue(dto.getValue());
            existingRating.setRatingDate(LocalDate.now());
            return albumRatingRepository.save(existingRating);
        } else {
            AlbumRating rating = new AlbumRating();
            rating.setValue(dto.getValue());
            rating.setUser(user);
            rating.setAlbum(album);
            rating.setRatingDate(LocalDate.now());
            return albumRatingRepository.save(rating);
        }
    }

    public void deleteRating(int id) {
        AlbumRating rating = findById(id);
        albumRatingRepository.delete(rating);
    }

    public AlbumRatingDto mapToDto(AlbumRating rating) {
        AlbumRatingDto dto = new AlbumRatingDto();
        dto.setId(rating.getId());
        dto.setValue(rating.getValue());
        if (rating.getUser() != null) {
            dto.setUserId(rating.getUser().getId());
            dto.setUserLogin(rating.getUser().getLogin());
        }
        if (rating.getAlbum() != null) {
            dto.setAlbumId(rating.getAlbum().getId());
            dto.setAlbumName(rating.getAlbum().getName());
        }
        dto.setRatingDate(rating.getRatingDate());
        return dto;
    }
}

