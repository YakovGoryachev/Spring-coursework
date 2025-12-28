package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {
    List<Rating> findByTrack_Id(int trackId);

    List<Rating> findByUser_Id(int userId);

    Rating findByUser_IdAndTrack_Id(int userId, int trackId);
}
