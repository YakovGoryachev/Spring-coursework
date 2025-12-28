package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByUser_Id(int userId);

    List<Playlist> findByUser_IdAndNameContainingIgnoreCase(int userId, String name);
    Playlist findByName(String name);

    @Query("SELECT COUNT(t) FROM Track t " +
            "JOIN t.playlists p " +
            "JOIN p.user u " +
            "WHERE u.id = :userId AND p.name = :playlistName")
    Long countTracksInPlaylistByUserIdAndName(@Param("userId") int userId, @Param("playlistName") String playlistName);
}
