package com.goryachev.music_service.Controllers;

import ch.qos.logback.core.spi.FilterReply;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.PlaylistRepository;
import com.goryachev.music_service.Repository.TrackRepository;
import com.goryachev.music_service.Repository.UserRepository;
import com.goryachev.music_service.Service.PlaylistService;
import com.goryachev.music_service.Service.TrackService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MyMusicController {
    private UserRepository userRepository;
    private PlaylistService playlistService;
    private PlaylistRepository playlistRepository;
    private TrackRepository trackRepository;
    private TrackService trackService;

    public MyMusicController(UserRepository userRepository, PlaylistService playlistService,
                             TrackService trackService, TrackRepository trackRepository,
                             PlaylistRepository playlistRepository){
        this.userRepository = userRepository;
        this.playlistService = playlistService;
        this.trackService = trackService;
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return userRepository.findByLogin(auth.getName());
        }
        return null;
    }

    @GetMapping("/my")
    public String myMusic(@RequestParam(required = false) String search,
                          @RequestParam(required = false) String searchCategory,
                          @RequestParam(defaultValue = "1") int page,
                          @RequestParam(defaultValue = "20") int size,
                          Model model) {

        Long quantTracks = 0L;
        Integer allDuration = 0;
        User currentUser = getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("content", "my-music :: content");
        model.addAttribute("title", "my music");

        List<TrackDto> allTracks = trackService.findByUserPlaylists(currentUser.getId()).stream().limit(100)
                .map(trackService::mapToDto)
                .collect(Collectors.toList());

        //quantTracks = allTracks.size();
        //quantTracks = playlistRepository.countTracksInPlaylistByUserIdAndName(currentUser.getId(), "Моя музыка");
        quantTracks = trackRepository.countDistinctTracksInUserPlaylists(currentUser.getId());

        if (search != null && !search.trim().isEmpty() && searchCategory != null && !searchCategory.isEmpty()) {
            switch (searchCategory) {
                case "title":
                    allTracks = trackService.findUserPlaylistTracksByTitle(currentUser.getId(), search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "artist":
                    allTracks = trackService.findUserPlaylistTracksByArtist(currentUser.getId(), search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "album":
                    allTracks = trackService.findUserPlaylistTracksByAlbum(currentUser.getId(), search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "genre":
                    allTracks = trackService.findUserPlaylistTracksByGenre(currentUser.getId(), search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "group":
                    allTracks = trackService.findUserPlaylistTracksByGroup(currentUser.getId(), search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                default:
                    allTracks = trackService.findByUserPlaylists(currentUser.getId()).stream().map(trackService::mapToDto).collect(Collectors.toList());
            }
        }

        int totalElements = allTracks.size();
        int totalPages = totalElements > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);

        List<TrackDto> pageTracks = new ArrayList<>();
        if (start < totalElements && !allTracks.isEmpty()) {
            pageTracks = allTracks.subList(start, end);
        }

        model.addAttribute("tracks", pageTracks);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("searchCategory", searchCategory);
        model.addAttribute("quantTracks", quantTracks);

        return "layout";
    }
}
