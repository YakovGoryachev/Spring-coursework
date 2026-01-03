package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.AlbumDto;
import com.goryachev.music_service.Pojo.Album;
import com.goryachev.music_service.Pojo.AlbumRating;
import com.goryachev.music_service.Pojo.Group;
import com.goryachev.music_service.Repository.AlbumRepository;
import com.goryachev.music_service.Repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public AlbumService(AlbumRepository albumRepository, GroupRepository groupRepository) {
        this.albumRepository = albumRepository;
        this.groupRepository = groupRepository;
    }

    public Album findById(int id) {
        return albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Альбом не найден"));
    }

    public Album findByName(String name) {
        return albumRepository.findByName(name);
    }

    public List<Album> findAll() {
        return albumRepository.findAll();
    }

    public List<Album> findByGroupId(int groupId) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
        return albumRepository.findByGroup_Id(groupId);
    }

    public Album createAlbum(AlbumDto dto) {
        Album album = new Album();
        album.setName(dto.getName());
        album.setReleaseDate(dto.getReleaseDate());
        album.setPlayCount(dto.getPlayCount() != null ? dto.getPlayCount() : 0);

        if (dto.getGroupId() > 0) {
            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Группа не найдена"));
            album.setGroup(group);
        }

        return albumRepository.save(album);
    }

    public Album updateAlbum(int id, AlbumDto dto) {
        Album album = findById(id);

        if (dto.getName() != null) {
            album.setName(dto.getName());
        }
        if (dto.getReleaseDate() != null) {
            album.setReleaseDate(dto.getReleaseDate());
        }
        if (dto.getPlayCount() != null) {
            album.setPlayCount(dto.getPlayCount());
        }
        if (dto.getGroupId() > 0) {
            Group group = groupRepository.findById(dto.getGroupId())
                    .orElseThrow(() -> new RuntimeException("Группа не найдена"));
            album.setGroup(group);
        }

        return albumRepository.save(album);
    }

    public void deleteAlbum(int id) {
        Album album = findById(id);
        albumRepository.delete(album);
    }

    public void incrementPlayCount(int id) {
        Album album = findById(id);
        album.setPlayCount(album.getPlayCount() != null ? album.getPlayCount() + 1 : 1);
        albumRepository.save(album);
    }

    public AlbumDto mapToDto(Album album) {
        AlbumDto dto = new AlbumDto();
        dto.setId(album.getId());
        dto.setName(album.getName());
        dto.setReleaseDate(album.getReleaseDate());
        dto.setPlayCount(album.getPlayCount());
        
        if (album.getGroup() != null) {
            dto.setGroupId(album.getGroup().getId());
            dto.setGroupName(album.getGroup().getName());
        }

        if (album.getTracks() != null) {
            dto.setTrackCount(album.getTracks().size());
        } else {
            dto.setTrackCount(0);
        }

        if (album.getAlbumRatings() != null && !album.getAlbumRatings().isEmpty()) {
            double avgRating = album.getAlbumRatings().stream()
                    .mapToInt(AlbumRating::getValue)
                    .average()
                    .orElse(0.0);
            dto.setAverageRating(avgRating);
        }
        
        return dto;
    }
}

