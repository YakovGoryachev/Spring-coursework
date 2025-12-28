package com.goryachev.music_service.Repository;

import com.goryachev.music_service.Pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByLogin(String login);

    User findByEmail(String email);
}
