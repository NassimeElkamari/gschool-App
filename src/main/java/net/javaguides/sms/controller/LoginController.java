package net.javaguides.sms.controller;

import jakarta.servlet.http.HttpSession;
import net.javaguides.sms.entity.Utilisateur;
import net.javaguides.sms.service.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Return the login page
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password) {
        // Spring Security handles authentication, so no need for manual checks here
        return "redirect:/"; // Redirect to the home page after login
    }
}