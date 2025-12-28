package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.PlaylistDto;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.Playlist;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.UserRepository;
import com.goryachev.music_service.Service.FileStorageService;
import com.goryachev.music_service.Service.PlaylistService;
import com.goryachev.music_service.Service.TrackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final TrackService trackService;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public PlaylistController(PlaylistService playlistService, TrackService trackService,
                             UserRepository userRepository, FileStorageService fileStorageService) {
        this.playlistService = playlistService;
        this.trackService = trackService;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return userRepository.findByLogin(auth.getName());
        }
        return null;
    }

    @PostMapping("/create")
    public String createPlaylist(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam(required = false) MultipartFile coverFile,
                                RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            PlaylistDto dto = new PlaylistDto();
            dto.setName(name);
            dto.setDescription(description);
            if (coverFile != null && !coverFile.isEmpty()) {
                String avatarPath = fileStorageService.storeAvatar(coverFile, "playlist");
                dto.setAvatarPath(avatarPath);
            }
            playlistService.createPlaylist(dto, currentUser.getId());
            redirectAttributes.addFlashAttribute("success", "Плейлист успешно создан!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при создании плейлиста: " + e.getMessage());
        }
        return "redirect:/playlists";
    }

    @GetMapping("/{id}")
    public String viewPlaylist(@PathVariable int id,
                              @RequestParam(required = false) String search,
                              @RequestParam(required = false) String searchCategory,
                              @RequestParam(defaultValue = "1") int page,
                              @RequestParam(defaultValue = "20") int size,
                              Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Playlist playlist = playlistService.findById(id);
        if (playlist.getUser().getId() != currentUser.getId()) {
            return "redirect:/playlists";
        }

        model.addAttribute("content", "playlist-detail :: content");

        List<TrackDto> allTracks;
        if (search != null && !search.trim().isEmpty() && searchCategory != null && !searchCategory.isEmpty()) {
            switch (searchCategory) {
                case "title":
                    allTracks = trackService.findByPlaylistAndTitle(id, search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "artist":
                    allTracks = trackService.findByPlaylistAndArtist(id, search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "album":
                    allTracks = trackService.findByPlaylistAndAlbum(id, search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "genre":
                    allTracks = trackService.findByPlaylistAndGenre(id, search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "group":
                    allTracks = trackService.findByPlaylistAndGroup(id, search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                default:
                    allTracks = trackService.findByPlaylist(id).stream().map(trackService::mapToDto).collect(Collectors.toList());
            }
        } else {
            allTracks = trackService.findByPlaylist(id).stream().limit(100)
                    .map(trackService::mapToDto)
                    .collect(Collectors.toList());
        }

        // Пагинация
        int totalElements = allTracks.size();
        int totalPages = totalElements > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);

        List<TrackDto> pageTracks = new java.util.ArrayList<>();
        if (start < totalElements && !allTracks.isEmpty()) {
            pageTracks = allTracks.subList(Math.max(0, start), end);
        }

        model.addAttribute("playlist", playlistService.mapToDto(playlist));
        model.addAttribute("tracks", pageTracks);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("searchCategory", searchCategory);

        return "layout";
    }
    @GetMapping("/{id}/edit")
    public String updatePlaylist(@PathVariable int id, Model model){
        PlaylistDto plDto = playlistService.mapToDto(playlistService.findById(id));
        model.addAttribute("playlistDto", plDto);
        model.addAttribute("content", "playlist-edit :: content");

        return "layout";
    }
    @PostMapping("/{id}/update")
    public String updatePlaylist(@PathVariable int id, PlaylistDto pDto){
        playlistService.updatePlaylist(id, pDto);
        return "redirect:/playlists";
    }

    @PostMapping("/{id}/delete")
    public String deletePlaylist(@PathVariable int id, RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Playlist playlist = playlistService.findById(id);
        if (playlist.getUser().getId() != currentUser.getId()) {
            redirectAttributes.addFlashAttribute("error", "Нет доступа к этому плейлисту");
            return "redirect:/playlists";
        }

        // Нельзя удалить плейлист "Моя музыка"
        if ("Моя музыка".equals(playlist.getName())) {
            redirectAttributes.addFlashAttribute("error", "Нельзя удалить плейлист 'Моя музыка'");
            return "redirect:/playlists";
        }

        try {
            playlistService.deletePlaylist(id);
            redirectAttributes.addFlashAttribute("success", "Плейлист успешно удален!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении плейлиста: " + e.getMessage());
        }
        return "redirect:/playlists";
    }

    @PostMapping("/{id}/remove-track/{trackId}")
    public String removeTrackFromPlaylist(@PathVariable int id,
                                         @PathVariable int trackId,
                                         RedirectAttributes redirectAttributes) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        Playlist playlist = playlistService.findById(id);
        if (playlist.getUser().getId() != currentUser.getId()) {
            redirectAttributes.addFlashAttribute("error", "Нет доступа к этому плейлисту");
            return "redirect:/playlists";
        }

        try {
            playlistService.removeTrackFromPlaylist(id, trackId);
            redirectAttributes.addFlashAttribute("success", "Трек удален из плейлиста!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при удалении трека: " + e.getMessage());
        }
        return "redirect:/playlists/" + id;
    }

    @GetMapping("") //playlists
    public String playlists(@RequestParam(required = false) String search,
                            @RequestParam(defaultValue = "1") int page,
                            @RequestParam(defaultValue = "20") int size,
                            Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("content", "playlists :: content");

        List<PlaylistDto> allPlaylists;
        if (search != null && !search.trim().isEmpty()) {
            allPlaylists = playlistService.findByUserIdAndName(currentUser.getId(), search).stream()
                    .map(playlistService::mapToDto)
                    .collect(Collectors.toList());
        } else {
            allPlaylists = playlistService.findByUserId(currentUser.getId()).stream()
                    .map(playlistService::mapToDto)
                    .collect(Collectors.toList());
        }

        // Пагинация
        int totalElements = allPlaylists.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);

        List<PlaylistDto> pagePlaylists = new ArrayList<>();
        if (start < totalElements) {
            pagePlaylists = allPlaylists.subList(start, end);
        }

        model.addAttribute("playlists", pagePlaylists);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("searchQuery", search);

        return "layout";
    }

    @GetMapping("/new")
    public String newPlaylist(Model model) {
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("content", "playlist-form :: content");
        model.addAttribute("playlistDto", new PlaylistDto());
        return "layout";
    }
}