package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.GroupDto;
import com.goryachev.music_service.Pojo.Artist;
import com.goryachev.music_service.Pojo.Group;
import com.goryachev.music_service.Repository.ArtistRepository;
import com.goryachev.music_service.Repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final ArtistRepository artistRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, ArtistRepository artistRepository) {
        this.groupRepository = groupRepository;
        this.artistRepository = artistRepository;
    }

    public Group findById(int id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Группа не найдена"));
    }

    public List<Group> findAll() {
        return groupRepository.findAll();
    }

    public List<Group> findByNameContaining(String name) {
        return groupRepository.findByNameContainingIgnoreCase(name);
    }

    public Group createGroup(GroupDto dto) {
        Group group = new Group();
        group.setName(dto.getName());
        group.setAvatarPath(dto.getAvatarPath());

        return groupRepository.save(group);
    }

    public Group updateGroup(int id, GroupDto dto) {
        Group group = findById(id);

        if (dto.getName() != null) {
            group.setName(dto.getName());
        }
        if (dto.getAvatarPath() != null) {
            group.setAvatarPath(dto.getAvatarPath());
        }

        return groupRepository.save(group);
    }

    public void deleteGroup(int id) {
        Group group = findById(id);
        groupRepository.delete(group);
    }

    public void addArtistToGroup(int groupId, int artistId) {
        Group group = findById(groupId);
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Артист не найден"));

        if (artist.getGroups() == null) {
            artist.setGroups(new java.util.ArrayList<>());
        }
        if (group.getArtists() == null) {
            group.setArtists(new java.util.ArrayList<>());
        }

        if (!artist.getGroups().contains(group)) {
            artist.getGroups().add(group);
            artistRepository.save(artist);
        }
    }

    public void removeArtistFromGroup(int groupId, int artistId) {
        Group group = findById(groupId);
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new RuntimeException("Артист не найден"));

        if (artist.getGroups() == null) {
            artist.setGroups(new java.util.ArrayList<>());
        }

        artist.getGroups().remove(group);
        artistRepository.save(artist);
    }

    public GroupDto mapToDto(Group group) {
        GroupDto dto = new GroupDto();
        dto.setId(group.getId());
        dto.setName(group.getName());
        dto.setAvatarPath(group.getAvatarPath());

        if (group.getArtists() != null) {
            dto.setArtistCount(group.getArtists().size());
        } else {
            dto.setArtistCount(0);
        }

        if (group.getAlbums() != null) {
            dto.setAlbumCount(group.getAlbums().size());
        } else {
            dto.setAlbumCount(0);
        }
        
        return dto;
    }
}