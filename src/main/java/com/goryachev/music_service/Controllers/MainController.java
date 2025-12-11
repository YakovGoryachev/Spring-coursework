package com.goryachev.music_service.Controllers;

import ch.qos.logback.core.spi.FilterReply;
import com.goryachev.music_service.DTO.*;
import com.goryachev.music_service.Pojo.Track;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MainController {

    @GetMapping("/allTracks")
    public String tracks(Model model){
        model.addAttribute("content", "all-tracks :: content");

//        // 1. Создаем ВСЕ треки
//        List<ArtistDto> allAlbums = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            allAlbums.add(new ArtistDto(i, "name"+i));
//        }
//        int size = 20;
//        int page = 1;
//
//        // 2. Рассчитываем пагинацию
//        int totalElements = allAlbums.size();
//        int totalPages = (int) Math.ceil((double) totalElements / size);
//        int start = (page - 1) * size;
//        int end = Math.min(start + size, totalElements);
//
//        // 3. Берем только нужную страницу
//        List<ArtistDto> pageTracks = allAlbums.subList(start, end);
//
//        // 4. Передаем в модель
//        model.addAttribute("artists", pageTracks); // ← только 20 треков
//        model.addAttribute("totalElements", totalElements); // ← 100
//        model.addAttribute("totalPages", totalPages);// ← 5 страниц
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
        List<TrackDto> tl = new ArrayList<>();
        tl.add(new TrackDto(1, "name", "artist", "album", "group", "genre", 23, 200, "", ""));

        model.addAttribute("tracks", tl);

        return "layout";
    }

    @GetMapping("/my")
    public String myMusic(Model model){
        model.addAttribute("content", "my-music :: content");
        model.addAttribute("title", "my music");
        List<TrackDto> tl = new ArrayList<>();
        tl.add(new TrackDto(1, "name", "artist", "album", "group", "genre", 23, 200, "", ""));

        model.addAttribute("tracks", tl);
        return "layout";
    }

    @GetMapping("/")
    public String mainpage(Model model){
        model.addAttribute("content", "index :: content");
        List<TrackDto> tl = new ArrayList<>();
        tl.add(new TrackDto(1, "name", "artist", "album", "group", "genre", 23, 200, "", ""));

        model.addAttribute("tracks", tl);
        return "layout";
    }
    @GetMapping("/playlists")
    public String playlists(Model model){
        model.addAttribute("content", "playlists :: content");
        return "layout";
    }
    @GetMapping("/playlists/new")
    public String newPlaylist(Model model){
        model.addAttribute("content", "playlist-form :: content");
        model.addAttribute("playlistDto", new PlaylistDto());
        return "layout";
    }
    @GetMapping("/login")
    public String login(Model model){
        return "login";
    }
    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("user", new UserDto());
        return "register";
    }
    @GetMapping("/admin")
    public String admin(Model model){
        return "admin-panel";
    }
    @GetMapping("/admin/tracks")
    public String adminTracks(Model model){
        List<TrackDto> tl = new ArrayList<>();
        tl.add(new TrackDto(1, "name", "artist", "album", "group", "genre", 23, 200, "", ""));

        return "admin-tracks";
    }
}
