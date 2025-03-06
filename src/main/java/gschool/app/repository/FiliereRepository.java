package gschool.app.repository;

import gschool.app.entity.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface FiliereRepository extends JpaRepository<Filiere, Integer> {
    Page<Filiere> findAll(Pageable pageable);

    @Query("SELECT COUNT(e) FROM Etudiant e WHERE e.filiere.id = :filiereId")
    long countStudentsByFiliereId(Integer filiereId);
}
