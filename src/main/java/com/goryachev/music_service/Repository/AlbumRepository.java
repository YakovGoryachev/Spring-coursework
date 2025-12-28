package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {
    Album findByName(String name);

    List<Album> findByGroup_Id(int groupId);

    List<Album> findByNameContainingIgnoreCase(String name);

    Album findByNameAndGroupId(String name, int groupId);
}
