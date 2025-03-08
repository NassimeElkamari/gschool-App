package gschool.app.controller;

import gschool.app.config.AgeCalculator;
import gschool.app.entity.Etudiant;
import gschool.app.entity.Utilisateur;
import gschool.app.repository.UtilisateurRepository;
import gschool.app.service.EtudiantService;
import gschool.app.service.FiliereService;
import gschool.app.service.UtilisateurService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class DashboardController {

    private final EtudiantService etudiantService;
    private final FiliereService filiereService;
    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;


    public DashboardController(EtudiantService etudiantService, FiliereService filiereService, UtilisateurService utilisateurService, UtilisateurRepository utilisateurRepository) {
        this.etudiantService = etudiantService;
        this.filiereService = filiereService;
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            String email = ((Utilisateur) authentication.getPrincipal()).getEmail();

            Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            LocalDateTime derniereConnexion = utilisateur.getDerniereConnexion();

            model.addAttribute("userName", username);
            model.addAttribute("userEmail", email);
            model.addAttribute("derniereConnexion", derniereConnexion);
        }
        List<Object[]> studentCountsByFiliere = etudiantService.getStudentCountByFiliere();
        String[] filieres = new String[studentCountsByFiliere.size()];
        Integer[] nombreEtudiants = new Integer[studentCountsByFiliere.size()];

        for (int i = 0; i < studentCountsByFiliere.size(); i++) {
            filieres[i] = (String) studentCountsByFiliere.get(i)[0];
            nombreEtudiants[i] = ((Long) studentCountsByFiliere.get(i)[1]).intValue();
        }

        List<Etudiant> allStudents = etudiantService.getAllEtudiants();
        Map<String, Long> ageDistribution = allStudents.stream()
                .collect(Collectors.groupingBy(
                        student -> {
                            String dateNaissance = student.getDateNaissance();
                            if (dateNaissance == null || dateNaissance.isEmpty()) {
                                return "Unknown";
                            }
                            try {
                                int age = AgeCalculator.calculateAge(dateNaissance);
                                return getAgeGroup(age);
                            } catch (Exception e) {
                                return "Unknown";
                            }
                        },
                        Collectors.counting()
                ));

        String[] ageGroups = ageDistribution.keySet().toArray(new String[0]);
        Long[] ageCounts = ageDistribution.values().toArray(new Long[0]);

        long etudiantCount = etudiantService.getAllEtudiants().size();
        long filiereCount = filiereService.getFilieres().size();
        long utilisateurCount = utilisateurService.getUtilisateurs().size();

        model.addAttribute("filieres", filieres);
        model.addAttribute("nombreEtudiants", nombreEtudiants);
        model.addAttribute("ageGroups", ageGroups);
        model.addAttribute("ageCounts", ageCounts);
        model.addAttribute("etudiantCount", etudiantCount);
        model.addAttribute("filiereCount", filiereCount);
        model.addAttribute("utilisateurCount", utilisateurCount);

        return "dashboard";
    }

    private String getAgeGroup(int age) {
        if (age >= 18 && age <= 20) {
            return "18-20 ans";
        } else if (age >= 21 && age <= 24) {
            return "21-24 ans";
        } else if (age >= 25 && age <= 30) {
            return "25-30 ans";
        } else {
            return "30+ ans";
        }
    }
}