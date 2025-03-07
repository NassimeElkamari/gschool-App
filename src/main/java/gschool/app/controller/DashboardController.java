package gschool.app.controller;

import gschool.app.entity.Utilisateur;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();  // Get the username (usually email)
            // Assuming your custom UserDetails class has a getEmail() method
            String email = ((Utilisateur) authentication.getPrincipal()).getEmail();

            // Debugging output
            System.out.println("Authenticated User: " + username + ", Email: " + email);

            model.addAttribute("userName", username);
            model.addAttribute("userEmail", email);
        }

        return "dashboard";  // Your Thymeleaf template
    }

}
