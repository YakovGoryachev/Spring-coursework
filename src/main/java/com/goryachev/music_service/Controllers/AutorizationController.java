package com.goryachev.music_service.Controllers;

import com.goryachev.music_service.DTO.UserDto;
import com.goryachev.music_service.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AutorizationController {

    private final UserService userService;

    @Autowired
    public AutorizationController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLogin(Model model){
        return "login";
    }

    @GetMapping("/register")
    public String showRegister(Model model){
        model.addAttribute("user", new UserDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") UserDto dto, RedirectAttributes redirectAttributes){
        try {
            userService.createUser(dto);
            redirectAttributes.addFlashAttribute("success", "Регистрация успешна! Теперь вы можете войти.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка при регистрации: " + e.getMessage());
            return "redirect:/register";
        }
    }
}
