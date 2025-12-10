package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.PlaylistDto;
import com.goryachev.music_service.DTO.TrackDto;
import com.goryachev.music_service.DTO.UserDto;
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
        return "layout";
    }

    @GetMapping("/my")
    public String myMusic(Model model){
        model.addAttribute("content", "my-music :: content");
        model.addAttribute("title", "my music");

        return "layout";
    }

    @GetMapping("/")
    public String mainpage(Model model){
        model.addAttribute("content", "index :: content");

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
        return "admin-tracks";
    }
}
