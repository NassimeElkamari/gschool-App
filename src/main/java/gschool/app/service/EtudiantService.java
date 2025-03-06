package gschool.app.service;

import gschool.app.entity.Etudiant;
import gschool.app.repository.EtudiantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;

    public EtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }

    public List<Etudiant> searchEtudiants(String name, String email, String code, String sort) {
        return etudiantRepository.searchEtudiants(name, email, code, sort);
    }

    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    public Page<Etudiant> getEtudiants(Pageable pageable){
        return etudiantRepository.findAll(pageable);
    }


    public Etudiant getEtudiantById(Integer id) {
        return etudiantRepository.findById(Long.valueOf(id)).orElse(null);
    }

    public void saveEtudiant(Etudiant etudiant) {
        etudiantRepository.save(etudiant);
    }

    public void updateEtudiant(Etudiant etudiant) {
        etudiantRepository.save(etudiant);
    }

    public void deleteEtudiantById(Integer id) {
        etudiantRepository.deleteById(Long.valueOf(id));
    }

    public List<Object[]> getStudentCountByFiliere() {
        return etudiantRepository.countStudentsByFiliere();
    }
}