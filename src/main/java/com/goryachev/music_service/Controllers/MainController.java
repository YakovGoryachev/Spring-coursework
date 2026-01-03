package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.*;
import com.goryachev.music_service.Pojo.Track;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.UserRepository;
import com.goryachev.music_service.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final TrackService trackService;
    private final UserRepository userRepository;
    private final PlaylistService playlistService;

    @Autowired
    public MainController(TrackService trackService, UserRepository userRepository, PlaylistService playlistService) {
        this.trackService = trackService;
        this.userRepository = userRepository;
        this.playlistService = playlistService;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return userRepository.findByLogin(auth.getName());
        }
        return null;
    }

    @GetMapping("/allTracks")
    public String tracks(@RequestParam(required = false) String search,
                         @RequestParam(required = false) String searchCategory,
                         @RequestParam(defaultValue = "1") int page,
                         @RequestParam(defaultValue = "20") int size,
                         Model model) {
        model.addAttribute("content", "all-tracks :: content");

        List<TrackDto> allTracks;
        if (search != null && !search.trim().isEmpty() && searchCategory != null && !searchCategory.isEmpty()) {
            switch (searchCategory) {
                case "title":
                    allTracks = trackService.findByNameContaining(search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "artist":
                    allTracks = trackService.findByArtistName(search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "album":
                    allTracks = trackService.findByAlbumName(search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "genre":
                    allTracks = trackService.findByGenre(search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                case "group":
                    allTracks = trackService.findByGroupName(search).stream().map(trackService::mapToDto).collect(Collectors.toList());
                    break;
                default:
                    allTracks = trackService.findAll().stream().map(trackService::mapToDto).collect(Collectors.toList());
            }
        } else {
            allTracks = trackService.findAll().stream()
                    .map(trackService::mapToDto)
                    .collect(Collectors.toList());
        }

        int totalElements = allTracks.size();
        int totalPages = totalElements > 0 ? (int) Math.ceil((double) totalElements / size) : 1;
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);

        List<TrackDto> pageTracks = new ArrayList<>();
        if (start < totalElements && !allTracks.isEmpty()) {
            pageTracks = allTracks.subList(start, end);
        }

//        Page<Track> trackPage = trackService.findAllPage(page, size);
//        List<Track> content = trackPage.getContent();
//        List<TrackDto> treki = content.stream().map(trackService::mapToDto).collect(Collectors.toList());

        model.addAttribute("tracks", pageTracks);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("totalPages", totalPages);
//        model.addAttribute("tracks", treki);
//        model.addAttribute("totalElements", trackPage.getTotalElements());
//        model.addAttribute("totalPages", trackPage.getTotalPages());
//        model.addAttribute("currentPage", trackPage.getNumber());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("search", search);
        model.addAttribute("searchCategory", searchCategory);

        return "layout";
    }

    @GetMapping("/")
    public String mainpage(Model model){
        model.addAttribute("content", "index :: content");
        List<TrackDto> tl = trackService.chartTracks();
        model.addAttribute("tracks", tl);
        return "layout";
    }
}
