package gschool.app.repository;

import gschool.app.entity.Filiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FiliereRepository extends JpaRepository<Filiere, Integer> {
    Page<Filiere> findAll(Pageable pageable);

}
