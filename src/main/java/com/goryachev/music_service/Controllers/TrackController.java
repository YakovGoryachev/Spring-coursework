package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.PlaylistDto;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.Playlist;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.UserRepository;
import com.goryachev.music_service.Service.PlaylistService;
import com.goryachev.music_service.Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tracks")
public class TrackController {

    private final PlaylistService playlistService;
    private final UserRepository userRepository;
    private TrackService trackService;

    @Autowired
    public TrackController(PlaylistService playlistService, UserRepository userRepository, TrackService trackService) {
        this.playlistService = playlistService;
        this.userRepository = userRepository;
        this.trackService = trackService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return userRepository.findByLogin(auth.getName());
        }
        return null;
    }

    @PostMapping("/{trackId}/add-to-playlist")
    public String addTrackToPlaylist(@PathVariable int trackId,
                                    @RequestParam String playlistName,
                                    @RequestParam(required = false) String returnUrl,
                                    RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            // Ищем плейлист по имени у пользователя
            Playlist playlist = playlistService.findByUserId(currentUser.getId()).stream()
                    .filter(p -> p.getName().equalsIgnoreCase(playlistName))
                    .findFirst()
                    .orElse(null);

            // Если плейлиста нет - создаем
            if (playlist == null) {
                PlaylistDto dto = new PlaylistDto();
                dto.setName(playlistName);
                playlist = playlistService.createPlaylist(dto, currentUser.getId());
            }

            // Добавляем трек в плейлист
            playlistService.addTrackToPlaylist(playlist.getId(), trackId);
            redirectAttributes.addFlashAttribute("success", "Трек добавлен в плейлист '" + playlistName + "'!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении трека: " + e.getMessage());
        }

        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/allTracks";
    }
}

