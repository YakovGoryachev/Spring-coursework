package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.ArtistDto;
import com.goryachev.music_service.Pojo.Artist;
import com.goryachev.music_service.Repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    @Autowired
    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public Artist findById(int id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Артист не найден"));
    }

    public List<Artist> findAll() {
        return artistRepository.findAll();
    }

    public List<Artist> findByNameContaining(String name) {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Artist createArtist(ArtistDto dto) {
        Artist artist = new Artist();
        artist.setName(dto.getName());
        artist.setAvatarPath(dto.getAvatarPath());
        artist.setBiography(dto.getBiography());

        return artistRepository.save(artist);
    }

    public Artist updateArtist(int id, ArtistDto dto) {
        Artist artist = findById(id);

        if (dto.getName() != null) {
            artist.setName(dto.getName());
        }
        if (dto.getAvatarPath() != null) {
            artist.setAvatarPath(dto.getAvatarPath());
        }
        if (dto.getBiography() != null) {
            artist.setBiography(dto.getBiography());
        }

        return artistRepository.save(artist);
    }

    public void deleteArtist(int id) {
        Artist artist = findById(id);
        artistRepository.delete(artist);
    }

    public ArtistDto mapToDto(Artist artist) {
        ArtistDto dto = new ArtistDto();
        dto.setId(artist.getId());
        dto.setName(artist.getName());
        dto.setAvatarPath(artist.getAvatarPath());
        dto.setBiography(artist.getBiography());
        return dto;
    }
}

