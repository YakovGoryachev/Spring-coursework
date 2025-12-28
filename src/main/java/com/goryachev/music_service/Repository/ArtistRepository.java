package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    List<Artist> findByNameContainingIgnoreCase(String name);

    Artist findFirstByNameIgnoreCase(String name);
}
