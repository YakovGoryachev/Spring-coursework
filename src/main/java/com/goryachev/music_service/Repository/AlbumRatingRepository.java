package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.AlbumRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRatingRepository extends JpaRepository<AlbumRating, Integer> {
    List<AlbumRating> findByAlbum_Id(int albumId);

    List<AlbumRating> findByUser_Id(int userId);

    AlbumRating findByUser_IdAndAlbum_Id(int userId, int albumId);
}
