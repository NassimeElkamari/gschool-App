package net.javaguides.sms.service;

import java.util.List;

import net.javaguides.sms.entity.Filiere;
import net.javaguides.sms.repository.EtudiantRepository;
import net.javaguides.sms.repository.FiliereRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class FiliereService {

    private final FiliereRepository filiereRepository
            ;

    public FiliereService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
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
        return filiereRepository.findById(id).get();
    }

    public void updateFiliere(Filiere filiere) {
        filiereRepository.save(filiere);
    }

    public void deleteFiliereById(Integer id) {
        filiereRepository.deleteById(id);
    }
}
