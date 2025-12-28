package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.AlbumDto;
import com.goryachev.music_service.DTO.ArtistDto;
import com.goryachev.music_service.DTO.GroupDto;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.Artist;
import com.goryachev.music_service.Pojo.Group;
import com.goryachev.music_service.Repository.AlbumRepository;
import com.goryachev.music_service.Repository.ArtistRepository;
import com.goryachev.music_service.Repository.GroupRepository;
import com.goryachev.music_service.Repository.TrackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final ArtistRepository artistRepository;
    private final GroupRepository groupRepository;
    private final AlbumRepository albumRepository;
    private final TrackRepository trackRepository;
    private final ArtistService artistService;
    private final GroupService groupService;
    private final AlbumService albumService;
    private final TrackService trackService;

    @Autowired
    public CatalogService(ArtistRepository artistRepository,
                         GroupRepository groupRepository,
                         AlbumRepository albumRepository,
                         TrackRepository trackRepository,
                         ArtistService artistService,
                         GroupService groupService,
                         AlbumService albumService,
                         TrackService trackService) {
        this.artistRepository = artistRepository;
        this.groupRepository = groupRepository;
        this.albumRepository = albumRepository;
        this.trackRepository = trackRepository;
        this.artistService = artistService;
        this.groupService = groupService;
        this.albumService = albumService;
        this.trackService = trackService;
    }

    public List<ArtistDto> searchArtists(String query) {
        if (query == null || query.trim().isEmpty()) {
            return artistRepository.findAll().stream()
                    .map(artistService::mapToDto)
                    .collect(Collectors.toList());
        }
        return artistService.findByNameContaining(query).stream()
                .map(artistService::mapToDto)
                .collect(Collectors.toList());
    }

    public ArtistDto getArtistWithDiscography(int artistId) {
        Artist artist = artistService.findById(artistId);
        return artistService.mapToDto(artist);
    }

    public List<AlbumDto> getArtistAlbums(int artistId) {
        Artist artist = artistService.findById(artistId);
        return artist.getGroups().stream()
                .flatMap(group -> group.getAlbums().stream())
                .map(albumService::mapToDto)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<AlbumDto> getGroupAlbums(int groupId) {
        return albumService.findByGroupId(groupId).stream()
                .map(albumService::mapToDto)
                .collect(Collectors.toList());
    }

    public GroupDto getGroupWithArtists(int groupId) {
        Group group = groupService.findById(groupId);
        return groupService.mapToDto(group);
    }

    public List<GroupDto> searchGroups(String query) {
        if (query == null || query.trim().isEmpty()) {
            return groupRepository.findAll().stream()
                    .map(groupService::mapToDto)
                    .collect(Collectors.toList());
        }
        return groupService.findByNameContaining(query).stream()
                .map(groupService::mapToDto)
                .collect(Collectors.toList());
    }

    public List<TrackDto> searchTracks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return trackRepository.findAll().stream()
                    .map(trackService::mapToDto)
                    .collect(Collectors.toList());
        }
        return trackService.findByNameContaining(query).stream()
                .map(trackService::mapToDto)
                .collect(Collectors.toList());
    }

    public List<AlbumDto> searchAlbums(String query) {
        if (query == null || query.trim().isEmpty()) {
            return albumRepository.findAll().stream()
                    .map(albumService::mapToDto)
                    .collect(Collectors.toList());
        }
        return albumRepository.findByNameContainingIgnoreCase(query).stream()
                .map(albumService::mapToDto)
                .collect(Collectors.toList());
    }
}
