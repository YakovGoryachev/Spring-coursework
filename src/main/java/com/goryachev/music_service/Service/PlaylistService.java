package com.goryachev.music_service.Service;

import ch.qos.logback.core.spi.FilterReply;
import com.goryachev.music_service.DTO.PlaylistDto;
import com.goryachev.music_service.Pojo.Playlist;
import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.PlaylistRepository;
import com.goryachev.music_service.Repository.TrackRepository;
import com.goryachev.music_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    @Autowired
    public PlaylistService(PlaylistRepository playlistRepository, 
                          UserRepository userRepository, 
                          TrackRepository trackRepository) {
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.trackRepository = trackRepository;
    }

    public Playlist findById(int id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Плейлист не найден"));
    }

    public List<Playlist> findByUserId(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return playlistRepository.findByUser_Id(userId);
    }

    public List<Playlist> findByUserIdAndName(int userId, String name) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        return playlistRepository.findByUser_IdAndNameContainingIgnoreCase(userId, name);
    }

    public List<Playlist> findAll() {
        return playlistRepository.findAll();
    }

    public Playlist createPlaylist(PlaylistDto dto, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        if (dto.getName().toLowerCase().contains("моя музыка")){
            throw new RuntimeException("Такой плейлист уже существует");
        }

        Playlist playlist = new Playlist();
        playlist.setName(dto.getName());
        playlist.setDescription(dto.getDescription());
        playlist.setPhotoUrl(dto.getAvatarPath());
        playlist.setUser(user);
        playlist.setCreationDate(LocalDateTime.now());
        playlist.setTracks(new java.util.ArrayList<>()); // Инициализируем пустой список

        return playlistRepository.save(playlist);
    }

    public Playlist updatePlaylist(int id, PlaylistDto dto) {
        Playlist playlist = findById(id);

        if (dto.getName() != null) {
            playlist.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            playlist.setDescription(dto.getDescription());
        }
        if (dto.getAvatarPath() != null) { //dorabotat
            playlist.setPhotoUrl(dto.getAvatarPath());
        }

        return playlistRepository.save(playlist);
    }

    public void deletePlaylist(int id) {
        Playlist playlist = findById(id);
        playlistRepository.delete(playlist);
    }

    public void addTrackToPlaylist(int playlistId, int trackId) {
        Playlist playlist = findById(playlistId);
        Playlist myPlaylist = playlistRepository.findByName("Моя музыка");

        if (myPlaylist == null){
            throw new RuntimeException("Моей музыки не существеут");
        }

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

         //Инициализируем список если null
        if (playlist.getTracks() == null) {
            playlist.setTracks(new java.util.ArrayList<>());
        }

        if (myPlaylist.getTracks() == null) {
            myPlaylist.setTracks(new java.util.ArrayList<>());
        }

        if (!playlist.getTracks().contains(track)) {
            playlist.getTracks().add(track);
            playlistRepository.save(playlist);
        }
        if (!myPlaylist.getTracks().contains(track)) {
            myPlaylist.getTracks().add(track);
            playlistRepository.save(myPlaylist);
        }
    }

    public void removeTrackFromPlaylist(int playlistId, int trackId) {
        Playlist playlist = findById(playlistId);

        Track track = trackRepository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Трек не найден"));

        playlist.getTracks().remove(track);
        playlistRepository.save(playlist);
    }

    public PlaylistDto mapToDto(Playlist playlist) {
        PlaylistDto dto = new PlaylistDto();
        dto.setId(playlist.getId());
        dto.setName(playlist.getName());
        dto.setDescription(playlist.getDescription());
        dto.setAvatarPath(playlist.getPhotoUrl());
        dto.setTrackCount(playlist.getTracks() != null ? playlist.getTracks().size() : 0);
        if (playlist.getCreationDate() != null) {
            dto.setCreatedAt(playlist.getCreationDate().toLocalDate());
        }
        if (playlist.getUser() != null) {
            dto.setUserId(playlist.getUser().getId());
            dto.setUserLogin(playlist.getUser().getLogin());
        }
        return dto;
    }
}
