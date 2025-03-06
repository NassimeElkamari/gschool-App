package gschool.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import gschool.app.entity.Student;

public interface StudentRepository extends JpaRepository<Student, Long>{

}
