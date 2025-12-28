package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.RatingDto;
import com.goryachev.music_service.Pojo.Rating;
import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.RatingRepository;
import com.goryachev.music_service.Repository.TrackRepository;
import com.goryachev.music_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository,
                        UserRepository userRepository,
                        TrackRepository trackRepository) {
        this.ratingRepository = ratingRepository;
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
    }

    public Rating findById(int id) {
        return ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Оценка не найдена"));
    }

    public List<Rating> findByTrackId(int trackId) {
        return ratingRepository.findByTrack_Id(trackId);
    }

    public List<Rating> findByUserId(int userId) {
        return ratingRepository.findByUser_Id(userId);
    }

    public Rating findByUserAndTrack(int userId, int trackId) {
        return ratingRepository.findByUser_IdAndTrack_Id(userId, trackId);
    }

    public double getAverageRatingForTrack(int trackId) {
        List<Rating> ratings = findByTrackId(trackId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        return ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);
    }

    public Rating createOrUpdateRating(RatingDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        Track track = trackRepository.findById(dto.getTrackId())
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

        Rating existingRating = findByUserAndTrack(dto.getUserId(), dto.getTrackId());

        if (existingRating != null) {
            existingRating.setValue(dto.getValue());
            existingRating.setRatingDate(LocalDate.now());
            return ratingRepository.save(existingRating);
        } else {
            Rating rating = new Rating();
            rating.setValue(dto.getValue());
            rating.setUser(user);
            rating.setTrack(track);
            rating.setRatingDate(LocalDate.now());
            return ratingRepository.save(rating);
        }
    }

    public void deleteRating(int id) {
        Rating rating = findById(id);
        ratingRepository.delete(rating);
    }

    public RatingDto mapToDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setValue(rating.getValue());
        if (rating.getUser() != null) {
            dto.setUserId(rating.getUser().getId());
            dto.setUserLogin(rating.getUser().getLogin());
        }
        if (rating.getTrack() != null) {
            dto.setTrackId(rating.getTrack().getId());
            dto.setTrackName(rating.getTrack().getName());
        }
        dto.setRatingDate(rating.getRatingDate());
        return dto;
    }
}

