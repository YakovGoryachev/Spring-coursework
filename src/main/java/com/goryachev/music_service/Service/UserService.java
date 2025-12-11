package com.goryachev.music_service.Service;

import com.goryachev.music_service.Pojo.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    public User findByLogin(String login){
        return new User();
    }
}
