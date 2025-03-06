package gschool.app.controller;

import gschool.app.entity.Etudiant;
import gschool.app.entity.Filiere;
import gschool.app.repository.EtudiantRepository;
import gschool.app.repository.FiliereRepository;
import gschool.app.repository.UtilisateurRepository;
import gschool.app.service.EtudiantService;
import gschool.app.service.FiliereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private FiliereService filiereService;

    @Autowired
    private EtudiantService etudiantService;

    private FiliereRepository filiereRepository;

    public DashboardController(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    @GetMapping
    public String ha(Model model) {
        List<Filiere> filieres = filiereRepository.findAll();
        List<String> filiereNames = filieres.stream().map(Filiere::getNomFiliere).collect(Collectors.toList());
        List<Integer> nombreEtudiants = filieres.stream().map(Filiere::getNombre_etudiant).collect(Collectors.toList());
        model.addAttribute("nombreEtudiants", nombreEtudiants);
        model.addAttribute("filieres", filiereNames);
        return "dashboard";
    }
}
