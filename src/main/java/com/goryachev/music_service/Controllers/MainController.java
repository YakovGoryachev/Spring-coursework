package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.*;
import com.goryachev.music_service.Pojo.Album;
import com.goryachev.music_service.Pojo.Artist;
import com.goryachev.music_service.Pojo.Group;
import com.goryachev.music_service.Pojo.User;
import com.goryachev.music_service.Repository.UserRepository;
import com.goryachev.music_service.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    private final TrackService trackService;
    private final UserRepository userRepository;
    private final PlaylistService playlistService;
    private final CommentService commentService;
    private final RatingService ratingService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final GroupService groupService;

    @Autowired
    public MainController(TrackService trackService, UserRepository userRepository, 
                         PlaylistService playlistService, CommentService commentService, RatingService ratingService,
                         ArtistService artistService, AlbumService albumService, GroupService groupService) {
        this.trackService = trackService;
        this.userRepository = userRepository;
        this.playlistService = playlistService;
        this.commentService = commentService;
        this.ratingService = ratingService;
        this.artistService = artistService;
        this.albumService = albumService;
        this.groupService = groupService;
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

    @GetMapping("/details/track/{id}")
    public String trackDetail(@PathVariable int id, Model model) {
        try {
            TrackDto trackDto = trackService.mapToDto(trackService.findById(id));
            model.addAttribute("track", trackDto);
            model.addAttribute("content", "track-detail :: content");
            model.addAttribute("pageTitle", trackDto.getName());

            List<CommentDto> comments = commentService.findByTrackId(id).stream()
                    .map(commentService::mapToDto)
                    .collect(Collectors.toList());
            model.addAttribute("comments", comments);

            double averageRating = ratingService.getAverageRatingForTrack(id);
            model.addAttribute("averageRating", averageRating);

            int ratingCount = ratingService.findByTrackId(id).size();
            model.addAttribute("ratingCount", ratingCount);

            User currentUser = getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("userPlaylists", playlistService.findByUserId(currentUser.getId()).stream()
                        .map(playlistService::mapToDto)
                        .collect(Collectors.toList()));
                
                var userRating = ratingService.findByUserAndTrack(currentUser.getId(), id);
                if (userRating != null) {
                    model.addAttribute("userRating", userRating.getValue());
                }
            }

            return "layout";
        } catch (Exception e) {
            return "redirect:/allTracks";
        }
    }

    @GetMapping("/details/artist/{id}")
    public String artistDetail(@PathVariable int id, Model model) {
        try {
            Artist artist = artistService.findById(id);
            ArtistDto artistDto = artistService.mapToDto(artist);
            model.addAttribute("artist", artistDto);
            model.addAttribute("content", "artist-detail :: content");
            model.addAttribute("pageTitle", artistDto.getName());

            List<TrackDto> tracks = trackService.findByArtistName(artist.getName()).stream()
                    .map(trackService::mapToDto)
                    .collect(Collectors.toList());
            model.addAttribute("tracks", tracks);

            if (artist.getGroups() != null) {
                List<GroupDto> groups = artist.getGroups().stream()
                        .map(groupService::mapToDto)
                        .collect(Collectors.toList());
                model.addAttribute("groups", groups);
            }

            User currentUser = getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("userPlaylists", playlistService.findByUserId(currentUser.getId()).stream()
                        .map(playlistService::mapToDto)
                        .collect(Collectors.toList()));
            }

            return "layout";
        } catch (Exception e) {
            return "redirect:/allTracks";
        }
    }

    @GetMapping("/details/album/{id}")
    public String albumDetail(@PathVariable int id, Model model) {
        try {
            Album album = albumService.findById(id);
            AlbumDto albumDto = albumService.mapToDto(album);
            model.addAttribute("album", albumDto);
            model.addAttribute("content", "album-detail :: content");
            model.addAttribute("pageTitle", albumDto.getName());

            List<TrackDto> tracks = trackService.findByAlbumId(id).stream()
                    .map(trackService::mapToDto)
                    .collect(Collectors.toList());
            model.addAttribute("tracks", tracks);

            User currentUser = getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("userPlaylists", playlistService.findByUserId(currentUser.getId()).stream()
                        .map(playlistService::mapToDto)
                        .collect(Collectors.toList()));
            }

            return "layout";
        } catch (Exception e) {
            return "redirect:/allTracks";
        }
    }

    @GetMapping("/details/group/{id}")
    public String groupDetail(@PathVariable int id, Model model) {
        try {
            Group group = groupService.findById(id);
            GroupDto groupDto = groupService.mapToDto(group);
            model.addAttribute("group", groupDto);
            model.addAttribute("content", "group-detail :: content");
            model.addAttribute("pageTitle", groupDto.getName());

            List<TrackDto> tracks = trackService.findByGroupName(group.getName()).stream()
                    .map(trackService::mapToDto)
                    .collect(Collectors.toList());
            model.addAttribute("tracks", tracks);

            if (group.getArtists() != null) {
                List<ArtistDto> artists = group.getArtists().stream()
                        .map(artistService::mapToDto)
                        .collect(Collectors.toList());
                model.addAttribute("artists", artists);
            }

            if (group.getAlbums() != null) {
                List<AlbumDto> albums = group.getAlbums().stream()
                        .map(albumService::mapToDto)
                        .collect(Collectors.toList());
                model.addAttribute("albums", albums);
            }

            User currentUser = getCurrentUser();
            if (currentUser != null) {
                model.addAttribute("userPlaylists", playlistService.findByUserId(currentUser.getId()).stream()
                        .map(playlistService::mapToDto)
                        .collect(Collectors.toList()));
            }

            return "layout";
        } catch (Exception e) {
            return "redirect:/allTracks";
        }
    }
}
