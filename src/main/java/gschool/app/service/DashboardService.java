package gschool.app.service;

import gschool.app.repository.EtudiantRepository;
import gschool.app.repository.FiliereRepository;
import gschool.app.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public Long getTotalEtudiants() {
        return etudiantRepository.count();
    }

    public Long getTotalFilieres() {
        return filiereRepository.count();
    }

    public Long getTotalUtilisateurs() {
        return utilisateurRepository.count();
    }

    public Map<String, Long> getEtudiantsByFiliere() {
        return etudiantRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(etudiant -> etudiant.getFiliere().getNomFiliere(), Collectors.counting()));
    }
}
