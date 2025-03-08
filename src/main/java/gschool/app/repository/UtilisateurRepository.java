package gschool.app.repository;

import gschool.app.entity.Utilisateur;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByEmail(String email);
    Page<Utilisateur> findAll(Pageable pageable);
    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);


}