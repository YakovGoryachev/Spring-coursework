package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.AlbumRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRatingRepository extends JpaRepository<AlbumRating, Integer> {
}
