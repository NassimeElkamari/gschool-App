package net.javaguides.sms.service;

import java.util.List;

import jakarta.transaction.Transactional;
import net.javaguides.sms.entity.Utilisateur;
import net.javaguides.sms.repository.UtilisateurRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private UtilisateurRepository utilisateurRepository;
    private PasswordEncoder passwordEncoder; // Inject the PasswordEncoder

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<Utilisateur> getUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }
    public boolean checkPassword(Utilisateur utilisateur, String rawPassword) {
        return passwordEncoder.matches(rawPassword, utilisateur.getMotDePasse());
    }

    public void saveUtilisateur(Utilisateur utilisateur) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        utilisateur.setMotDePasse(encoder.encode(utilisateur.getMotDePasse())); // Encrypt password
        utilisateurRepository.save(utilisateur);
    }

    public Page<Utilisateur> getUtilisateurs(Pageable pageable){
        return utilisateurRepository.findAll(pageable);
    }

    public Utilisateur getUtilisateurById(Integer id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    public void updateUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void deleteUtilisateurById(Integer id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur not found"));

        // Log the entity state
        System.out.println("Deleting Utilisateur: " + utilisateur);
        System.out.println("Email: " + utilisateur.getEmail());

        if (utilisateur.getEmail() == null) {
            throw new RuntimeException("Email is null for Utilisateur with ID: " + id);
        }

        utilisateurRepository.delete(utilisateur);
    }

}