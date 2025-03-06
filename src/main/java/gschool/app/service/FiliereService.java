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
    private final EtudiantRepository etudiantRepository; // Inject EtudiantRepository

    public FiliereService(FiliereRepository filiereRepository, EtudiantRepository etudiantRepository) {
        this.filiereRepository = filiereRepository;
        this.etudiantRepository = etudiantRepository;
    }

    // Fetching filieres with pagination
    public Page<Filiere> getFilieres(Pageable pageable) {
        return filiereRepository.findAll(pageable);
    }

    // Fetching all filieres
    public List<Filiere> getFilieres() {
        return filiereRepository.findAll();
    }

    // Saving a new Filiere
    public void saveFiliere(Filiere filiere) {
        filiereRepository.save(filiere);
    }

    // Fetching Filiere by ID
    public Filiere getFiliereById(Integer id) {
        return filiereRepository.findById(id).orElse(null);
    }

    // Updating an existing Filiere
    public void updateFiliere(Filiere filiere) {
        filiereRepository.save(filiere);
    }

    // Deleting Filiere by ID
    public void deleteFiliereById(Integer id) {
        filiereRepository.deleteById(id);
    }

    // New method to get the number of students in a specific Filiere
    public Integer getNumberOfStudentsInFiliere(Integer filiereId) {
        return etudiantRepository.countByFiliereId(filiereId); // Assuming this method exists in EtudiantRepository
    }
}
