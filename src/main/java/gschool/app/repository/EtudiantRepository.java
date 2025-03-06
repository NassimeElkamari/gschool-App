package gschool.app.repository;

import gschool.app.entity.Etudiant;
import gschool.app.entity.Filiere;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    @Query("SELECT e FROM Etudiant e WHERE " +
            "(:name IS NULL OR e.nom LIKE %:name%) AND " +
            "(:email IS NULL OR e.email LIKE %:email%) AND " +
            "(:code IS NULL OR e.codeEtudiant LIKE %:code%) " +
            "ORDER BY " +
            "CASE WHEN :sort = 'nom' THEN e.nom END ASC, " +
            "CASE WHEN :sort = 'nom_desc' THEN e.nom END DESC, " +
            "CASE WHEN :sort = 'email' THEN e.email END ASC, " +
            "CASE WHEN :sort = 'email_desc' THEN e.email END DESC, " +
            "CASE WHEN :sort = 'code' THEN e.codeEtudiant END ASC, " +
            "CASE WHEN :sort = 'code_desc' THEN e.codeEtudiant END DESC")
    List<Etudiant> searchEtudiants(@Param("name") String name,
                                   @Param("email") String email,
                                   @Param("code") String code,
                                   @Param("sort") String sort);

    Page<Etudiant> findAll(Pageable pageable);

    // Count the number of students in a specific filière
    @Query("SELECT COUNT(e) FROM Etudiant e WHERE e.filiere = :filiere")
    long countByFiliere(@Param("filiere") Filiere filiere);

    // Fetch the 5 most recent students
    @Query("SELECT e FROM Etudiant e ORDER BY e.id DESC LIMIT 5")
    List<Etudiant> findTop5ByOrderByIdDesc();

    Integer countByFiliereId(Integer filiereId);

    @Query("SELECT e.filiere.nomFiliere, COUNT(e) FROM Etudiant e GROUP BY e.filiere")
    List<Object[]> countStudentsByFiliere();


}

