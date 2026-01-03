package com.goryachev.music_service.Service;

import com.goryachev.music_service.DTO.PlaylistDto;
import com.goryachev.music_service.DTO.UserDto;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlaylistService playlistService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, PlaylistService playlistService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.playlistService = playlistService;
    }

    public User findByLogin(String login) {
        return userRepository.findByLogin(login);
    }

    public User findById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public User createUser(UserDto dto) {
        if (userRepository.findByLogin(dto.getLogin()) != null) {
            throw new RuntimeException("Пользователь с таким логином уже существует");
        }
        if (findByEmail(dto.getEmail()) != null) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        User user = new User();
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setDateBorn(dto.getDateBorn());
        user.setAdmin(false);

        User savedUser = userRepository.save(user);

        PlaylistDto myMusicDto = new PlaylistDto();
        myMusicDto.setName("Моя музыка");
        myMusicDto.setDescription("Все ваши треки");
        playlistService.createPlaylist(myMusicDto, savedUser.getId());

        return savedUser;
    }

    public User updateUser(int id, UserDto dto) {
        User user = findById(id);
        
        if (dto.getLogin() != null && !dto.getLogin().equals(user.getLogin())) {
            User existing = userRepository.findByLogin(dto.getLogin());
            if (existing != null && existing.getId() != id) {
                throw new RuntimeException("Пользователь с таким логином уже существует");
            }
            user.setLogin(dto.getLogin());
        }
        
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            User existing = findByEmail(dto.getEmail());
            if (existing != null && existing.getId() != id) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }
            user.setEmail(dto.getEmail());
        }
        
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        
        user.setAdmin(dto.isAdmin());

        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    public UserDto mapToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        dto.setAdmin(user.isAdmin());
        dto.setDateRegister(user.getDateRegister());
        return dto;
    }
    
    public UserDto mapToDtoWithPassword(User user) {
        UserDto dto = mapToDto(user);
        dto.setPassword(user.getPassword());
        return dto;
    }
}
