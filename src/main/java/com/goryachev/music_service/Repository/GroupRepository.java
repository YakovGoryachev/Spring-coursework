package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<Group, Integer> {
    List<Group> findByNameContainingIgnoreCase(String name);

    Group findFirstByNameIgnoreCase(String name);
}
