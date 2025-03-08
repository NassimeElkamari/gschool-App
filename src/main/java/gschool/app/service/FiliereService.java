package gschool.app.service;

import java.util.List;

import gschool.app.entity.Filiere;
import gschool.app.repository.EtudiantRepository; // Ensure you have this repository for Etudiant (Student)
import gschool.app.repository.FiliereRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository;
    private final EtudiantRepository etudiantRepository;

    public FiliereService(FiliereRepository filiereRepository, EtudiantRepository etudiantRepository) {
        this.filiereRepository = filiereRepository;
        this.etudiantRepository = etudiantRepository;
    }

    public Page<Filiere> getFilieres(Pageable pageable) {
        return filiereRepository.findAll(pageable);
    }

    public List<Filiere> getFilieres() {
        return filiereRepository.findAll();
    }

    public void saveFiliere(Filiere filiere) {
        filiereRepository.save(filiere);
    }

    public Filiere getFiliereById(Integer id) {
        return filiereRepository.findById(id).orElse(null);
    }

    public void updateFiliere(Filiere filiere) {
        filiereRepository.save(filiere);
    }

    public void deleteFiliereById(Integer id) {
        filiereRepository.deleteById(id);
    }

    public Integer getNumberOfStudentsInFiliere(Integer filiereId) {
        return etudiantRepository.countByFiliereId(filiereId); // Assuming this method exists in EtudiantRepository
    }
}
