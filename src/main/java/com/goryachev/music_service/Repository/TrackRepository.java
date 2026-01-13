package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrackRepository extends JpaRepository<Track, Integer> {

    @Query("SELECT COUNT(DISTINCT t) FROM Track t JOIN t.playlists p WHERE p.user.id = :userId")
    long countDistinctTracksInUserPlaylists(@Param("userId") int userId);

    @Query("select t from Track t")
    Page<Track> findAllPage(Pageable pageable);

    List<Track> findByAlbum_Id(int albumId);

    List<Track> findByNameContainingIgnoreCase(String name);

    @Query("SELECT t FROM Track t " +
            "JOIN t.album a " +
            "WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :albumName, '%'))")
    List<Track> findByAlbum_NameContainingIgnoreCase(@Param("albumName") String albumName);

    List<Track> findByGenreContainingIgnoreCase(String genre);

    @Query("SELECT t FROM Track t " +
            "JOIN t.album a " +
            "JOIN a.group g " +
            "WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%'))")
    List<Track> findByAlbum_Group_NameContainingIgnoreCase(@Param("groupName") String groupName);

    @Query("select distinct t from Track t join t.album a join a.group g join g.artists ar where lower(ar.name) like lower(concat('%', :artistName, '%'))")
    List<Track> findByArtistName(@Param("artistName") String artistName);

    @Query("select distinct t from Track t join t.playlists p where p.user.id = :userId")
    List<Track> findDistinctByPlaylistsUserId(@Param("userId") int userId);

    List<Track> findByPlaylists_Id(int playlistId);

    List<Track> findByPlaylists_IdAndNameContainingIgnoreCase(int playlistId, String name);

    List<Track> findByPlaylists_IdAndAlbum_NameContainingIgnoreCase(int playlistId, String albumName);

    List<Track> findByPlaylists_IdAndGenreContainingIgnoreCase(int playlistId, String genre);

    List<Track> findByPlaylists_IdAndAlbum_Group_NameContainingIgnoreCase(int playlistId, String groupName);

    @Query("select distinct t from Track t join t.playlists p join t.album a join a.group g join g.artists ar where p.id = :playlistId and lower(ar.name) like lower(concat('%', :artistName, '%'))")
    List<Track> findByPlaylistIdAndArtistName(@Param("playlistId") int playlistId, @Param("artistName") String artistName);

    List<Track> findDistinctByPlaylists_User_Id(int userId);

    List<Track> findDistinctByPlaylists_User_IdAndNameContainingIgnoreCase(int userId, String name);

    @Query("SELECT DISTINCT t FROM Track t " +
            "JOIN t.playlists pt " +
            "JOIN pt.user u " +
            "JOIN t.album a " +
            "WHERE u.id = :userId " +
            "AND LOWER(a.name) LIKE LOWER(CONCAT('%', :albumName, '%'))")
    List<Track> findDistinctByPlaylists_User_IdAndAlbum_NameContainingIgnoreCase(@Param("userId") int userId, @Param("albumName") String albumName);

    List<Track> findDistinctByPlaylists_User_IdAndGenreContainingIgnoreCase(int userId, String genre);
    @Query("SELECT DISTINCT t FROM Track t " +
            "JOIN t.playlists pt " +
            "JOIN pt.user u " +
            "JOIN t.album a " +
            "JOIN a.group g " +
            "WHERE u.id = :userId " +
            "AND LOWER(g.name) LIKE LOWER(CONCAT('%', :groupName, '%'))")

    List<Track> findDistinctByPlaylists_User_IdAndAlbum_Group_NameContainingIgnoreCase(@Param("userId") int userId, @Param("groupName") String groupName);

    @Query("select distinct t from Track t join t.playlists p join t.album a join a.group g join g.artists ar where p.user.id = :userId and lower(ar.name) like lower(concat('%', :artistName, '%'))")
    List<Track> findDistinctByPlaylistsUserIdAndArtist(@Param("userId") int userId, @Param("artistName") String artistName);
    List<Track> findTop20ByOrderByPlayCountDesc();
}
