package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.CommentDto;
import com.goryachev.music_service.DTO.PlaylistDto;
import com.goryachev.music_service.DTO.RatingDto;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.Playlist;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.UserRepository;
import com.goryachev.music_service.Service.CommentService;
import com.goryachev.music_service.Service.PlaylistService;
import com.goryachev.music_service.Service.RatingService;
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
    private final TrackService trackService;
    private final CommentService commentService;
    private final RatingService ratingService;

    @Autowired
    public TrackController(PlaylistService playlistService, UserRepository userRepository, 
                          TrackService trackService, CommentService commentService, RatingService ratingService) {
        this.playlistService = playlistService;
        this.userRepository = userRepository;
        this.trackService = trackService;
        this.commentService = commentService;
        this.ratingService = ratingService;
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
            Playlist playlist = playlistService.findByUserId(currentUser.getId()).stream()
                    .filter(p -> p.getName().equalsIgnoreCase(playlistName))
                    .findFirst()
                    .orElse(null);

            if (playlist == null) {
                PlaylistDto dto = new PlaylistDto();
                dto.setName(playlistName);
                playlist = playlistService.createPlaylist(dto, currentUser.getId());
            }

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

    @PostMapping("/ratings/track/{trackId}")
    public String addRating(@PathVariable int trackId,
                            @RequestParam int rating,
                            @RequestParam(required = false) String returnUrl,
                            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Необходимо войти в систему для оценки трека");
            return "redirect:/login";
        }

        try {
            RatingDto ratingDto = new RatingDto();
            ratingDto.setUserId(currentUser.getId());
            ratingDto.setTrackId(trackId);
            ratingDto.setValue(rating);
            ratingService.createOrUpdateRating(ratingDto);
            redirectAttributes.addFlashAttribute("success", "Оценка успешно добавлена!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении оценки: " + e.getMessage());
        }

        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/details/track/" + trackId;
    }

    @PostMapping("/comments/track/{trackId}")
    public String addComment(@PathVariable int trackId,
                            @RequestParam String text,
                            @RequestParam(required = false) String returnUrl,
                            RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            redirectAttributes.addFlashAttribute("error", "Необходимо войти в систему для добавления комментария");
            return "redirect:/login";
        }

        try {
            CommentDto commentDto = new CommentDto();
            commentDto.setUserId(currentUser.getId());
            commentDto.setTrackId(trackId);
            commentDto.setText(text);
            commentService.createComment(commentDto);
            redirectAttributes.addFlashAttribute("success", "Комментарий успешно добавлен!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при добавлении комментария: " + e.getMessage());
        }

        if (returnUrl != null && !returnUrl.isEmpty()) {
            return "redirect:" + returnUrl;
        }
        return "redirect:/details/track/" + trackId;
    }
}

