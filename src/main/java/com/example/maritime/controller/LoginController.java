package com.example.maritime.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin123";

    @GetMapping({"/", "/login"})
    public String loginPage(HttpSession session, Model model) {
        if (session.getAttribute("loggedIn") != null) {
            return "redirect:/dashboard";
        }
        model.addAttribute("loginMessage", "Welcome back");
        return "index";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username,
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (USERNAME.equals(username.trim()) && PASSWORD.equals(password.trim())) {
            session.setAttribute("loggedIn", true);
            session.setAttribute("username", username);
            return "redirect:/dashboard";
        }

        redirectAttributes.addFlashAttribute("error", "Invalid username or password");
        return "redirect:/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
