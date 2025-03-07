package gschool.app.controller;

import gschool.app.entity.Etudiant;
import gschool.app.entity.Filiere;
import gschool.app.entity.Utilisateur;
import gschool.app.repository.EtudiantRepository;
import gschool.app.repository.FiliereRepository;
import gschool.app.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class HomeController {

    private final EtudiantRepository etudiantRepository;
    private final FiliereRepository filiereRepository;
    private final UtilisateurRepository utilisateurRepository;

    public HomeController(EtudiantRepository etudiantRepository, FiliereRepository filiereRepository, UtilisateurRepository utilisateurRepository) {
        this.etudiantRepository = etudiantRepository;
        this.filiereRepository = filiereRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Fetch filieres and student count per filiere
        List<Filiere> filieres = filiereRepository.findAll();
        List<String> filiereNames = filieres.stream().map(Filiere::getNomFiliere).collect(Collectors.toList());
        List<Long> etudiantsParFiliere = filieres.stream()
                .map(filiere -> etudiantRepository.countByFiliere(filiere))
                .collect(Collectors.toList());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();  // This now returns nomUtilisateur
            String email = ((Utilisateur) authentication.getPrincipal()).getEmail();

            // Add user info to the model
            model.addAttribute("userName", username);  // This is the user's name now
            model.addAttribute("userEmail", email);
        }

        // Add other attributes to the model for filieres and student counts
        model.addAttribute("nombreEtudiants", etudiantsParFiliere);
        model.addAttribute("filieres", filiereNames);

        return "layout";  // Your Thymeleaf template
    }

}
