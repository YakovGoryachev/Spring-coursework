package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.ObjectTrackDto;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.Album;
import com.goryachev.music_service.Pojo.Rating;
import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Repository.AlbumRepository;
import com.goryachev.music_service.Repository.ArtistRepository;
import com.goryachev.music_service.Repository.GroupRepository;
import com.goryachev.music_service.Repository.TrackRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrackService {

    private final TrackRepository trackRepository;
    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final GroupRepository groupRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public TrackService(TrackRepository trackRepository, FileStorageService fileStorageService,
                        AlbumRepository albumRepository, ArtistRepository artistRepository, 
                        GroupRepository groupRepository) {
        this.trackRepository = trackRepository;
        this.fileStorageService = fileStorageService;
        this.albumRepository = albumRepository;
        this.groupRepository = groupRepository;
        this.artistRepository = artistRepository;
    }

    public Track findById(int id) {
        return trackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));
    }

    public List<Track> findAll() {
        return trackRepository.findAll();
    }
    public Page<Track> findAllPage(int page, int size){ //was not
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return trackRepository.findAllPage(pageable);
    }

    public List<Track> findByAlbumId(int albumId) {
        return trackRepository.findByAlbum_Id(albumId);
    }

    public List<Track> findByNameContaining(String name) {
        return trackRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Track> findByArtistName(String artistName) {
        return trackRepository.findByArtistName(artistName);
    }

    public List<Track> findByAlbumName(String albumName) {
        return trackRepository.findByAlbum_NameContainingIgnoreCase(albumName);
    }

    public List<Track> findByGenre(String genre) {
        return trackRepository.findByGenreContainingIgnoreCase(genre);
    }

    public List<Track> findByGroupName(String groupName) {
        return trackRepository.findByAlbum_Group_NameContainingIgnoreCase(groupName);
    }

    public List<Track> findByUserPlaylists(int userId) {
        return trackRepository.findDistinctByPlaylistsUserId(userId);
    }

    public List<Track> findByPlaylist(int playlistId) {
        return trackRepository.findByPlaylists_Id(playlistId);
    }

    public List<Track> findByPlaylistAndTitle(int playlistId, String title) {
        return trackRepository.findByPlaylists_IdAndNameContainingIgnoreCase(playlistId, title);
    }

    public List<Track> findByPlaylistAndArtist(int playlistId, String artist) {
        return trackRepository.findByPlaylistIdAndArtistName(playlistId, artist);
    }

    public List<Track> findByPlaylistAndAlbum(int playlistId, String album) {
        return trackRepository.findByPlaylists_IdAndAlbum_NameContainingIgnoreCase(playlistId, album);
    }

    public List<Track> findByPlaylistAndGenre(int playlistId, String genre) {
        return trackRepository.findByPlaylists_IdAndGenreContainingIgnoreCase(playlistId, genre);
    }

    public List<Track> findByPlaylistAndGroup(int playlistId, String group) {
        return trackRepository.findByPlaylists_IdAndAlbum_Group_NameContainingIgnoreCase(playlistId, group);
    }

    public List<Track> findUserPlaylistTracksByTitle(int userId, String title) {
        return trackRepository.findDistinctByPlaylists_User_IdAndNameContainingIgnoreCase(userId, title);
    }

    public List<Track> findUserPlaylistTracksByArtist(int userId, String artist) {
        return trackRepository.findDistinctByPlaylistsUserIdAndArtist(userId, artist);
    }

    public List<Track> findUserPlaylistTracksByAlbum(int userId, String album) {
        return trackRepository.findDistinctByPlaylists_User_IdAndAlbum_NameContainingIgnoreCase(userId, album);
    }

    public List<Track> findUserPlaylistTracksByGenre(int userId, String genre) {
        return trackRepository.findDistinctByPlaylists_User_IdAndGenreContainingIgnoreCase(userId, genre);
    }

    public List<Track> findUserPlaylistTracksByGroup(int userId, String group) {
        return trackRepository.findDistinctByPlaylists_User_IdAndAlbum_Group_NameContainingIgnoreCase(userId, group);
    }

    public List<TrackDto> chartTracks(){
        List<Track> tracks = trackRepository.findTop20ByOrderByPlayCountDesc();
        if (tracks.size() == 0){
            throw new RuntimeException("Треков нет");
        }
        List<TrackDto> trsDto = tracks.stream().map(track -> mapToDto(track)).collect(Collectors.toList());
        return trsDto ;
    }

    //с названиеями в trackdto, а не объектами
    public Track createTrack(TrackDto dto) {
        if (dto.getId() > 0) {
            throw new RuntimeException("Трек уже существует");
        }
        
        Track track = mapToTrackAdd(dto);
        return trackRepository.save(track);
    }
    public Track createWithObjectsTrack(ObjectTrackDto dto){
        if (dto.getId() > 0){
            throw new RuntimeException("Трек существует");
        }
        Track track = mapObjectsToTrackAdd(dto);
        return trackRepository.save(track);
    }

    public void addTrackWithFiles(TrackDto dto, MultipartFile audioFile, MultipartFile avatarFile, String login) {
        if (dto.getId() > 0) {
            throw new RuntimeException("Трек существует");
        }
        
        String audioPath = "./uploads/" + fileStorageService.storeAudio(audioFile);
        String avatarPath = null;
        if (avatarFile != null && !avatarFile.isEmpty()) {
            avatarPath = fileStorageService.storeAvatar(avatarFile, "track");
        }

        dto.setFilePath(audioPath);
        dto.setAvatarPath(avatarPath);

        createTrack(dto);
    }

    public Track updateTrack(TrackDto dto) {
        Track track = mapToTrackUpd(dto);
        return trackRepository.save(track);
    }

    public void deleteTrack(int id) {
        Track track = findById(id);
        
        // Удаляем файлы
        if (track.getFilePath() != null) {
            fileStorageService.deleteFile(track.getFilePath());
        }
        if (track.getAvatarPath() != null) {
            fileStorageService.deleteFile(track.getAvatarPath());
        }
        
        trackRepository.delete(track);
    }

    public void incrementPlayCount(int id) {
        Track track = findById(id);
        if (track == null){
            throw new EntityNotFoundException("Трек не найден - " + id);
        }

        track.setPlayCount(track.getPlayCount() != null ? track.getPlayCount() + 1 : 1);
        trackRepository.save(track);
    }

    public Track mapToTrackAdd(TrackDto dto) {
        Track track = new Track();
        track.setName(dto.getName());
        track.setGenre(dto.getGenre());
        track.setDuration(dto.getDuration());
        track.setPlayCount(dto.getPlayCount() != null ? dto.getPlayCount() : 0);
        track.setFilePath(dto.getFilePath());
        track.setAvatarPath(dto.getAvatarPath());

        if (dto.getAlbumName() != null && !dto.getAlbumName().isEmpty()) {
            Album album = albumRepository.findByName(dto.getAlbumName());
            if (album == null) {
                throw new RuntimeException("Альбом не найден: " + dto.getAlbumName());
            }
            track.setAlbum(album);
        } else {
            throw new RuntimeException("Альбом обязателен");
        }

        return track;
    }

    public Track mapObjectsToTrackAdd(ObjectTrackDto dto) {
        Track track = new Track();
        track.setName(dto.getName());
        track.setGenre(dto.getGenre());
        track.setDuration(dto.getDuration());
        track.setPlayCount(dto.getPlayCount() != null ? dto.getPlayCount() : 0);
        track.setFilePath(dto.getFilePath());
        track.setAvatarPath(dto.getAvatarPath());

        if (dto.getAlbum() != null) {
            Optional<Album> album = albumRepository.findById(dto.getAlbum().getId());
            if (album == null) {
                throw new RuntimeException("Альбом не найден: " + dto.getAlbum().getName());
            }
            track.setAlbum(dto.getAlbum());
        } else {
            throw new RuntimeException("Альбом обязателен");
        }

        return track;
    }

    public Track mapToTrackUpd(TrackDto dto) {
        Track track = findById(dto.getId());

        if (dto.getName() != null) {
            track.setName(dto.getName());
        }
        if (dto.getGenre() != null) {
            track.setGenre(dto.getGenre());
        }
        if (dto.getDuration() != null) {
            track.setDuration(dto.getDuration());
        }
        if (dto.getPlayCount() != null) {
            track.setPlayCount(dto.getPlayCount());
        }
        if (dto.getFilePath() != null) {
            track.setFilePath(dto.getFilePath());
        }
        if (dto.getAvatarPath() != null) {
            track.setAvatarPath(dto.getAvatarPath());
        }
        if (dto.getAlbumName() != null && !dto.getAlbumName().isEmpty()) {
            Album album = albumRepository.findByName(dto.getAlbumName());
            if (album == null) {
                throw new RuntimeException("Альбом не найден: " + dto.getAlbumName());
            }
            track.setAlbum(album);
        }

        return track;
    }

    public TrackDto mapToDto(Track track) {
        TrackDto dto = new TrackDto();
        dto.setId(track.getId());
        dto.setName(track.getName());
        dto.setGenre(track.getGenre());
        dto.setDuration(track.getDuration());
        dto.setPlayCount(track.getPlayCount());
        dto.setFilePath(track.getFilePath());
        dto.setAvatarPath(track.getAvatarPath());

        if (track.getAlbum() != null) {
            dto.setAlbumId(track.getAlbum().getId());
            dto.setAlbumName(track.getAlbum().getName());
            if (track.getAlbum().getGroup() != null) {
                dto.setGroupName(track.getAlbum().getGroup().getName());
                if (track.getAlbum().getGroup().getArtists() != null && !track.getAlbum().getGroup().getArtists().isEmpty()) {
                    dto.setArtistName(track.getAlbum().getGroup().getArtists().get(0).getName());
                }
            }
        }

        // Средний рейтинг
        if (track.getRatings() != null && !track.getRatings().isEmpty()) {
            double avgRating = track.getRatings().stream()
                    .mapToInt(Rating::getValue)
                    .average()
                    .orElse(0.0);
            dto.setAverageRating(avgRating);
        }

        // Количество комментариев
        if (track.getComments() != null) {
            dto.setCommentCount(track.getComments().size());
        } else {
            dto.setCommentCount(0);
        }

        return dto;
    }
}
