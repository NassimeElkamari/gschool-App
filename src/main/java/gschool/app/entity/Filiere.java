package gschool.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "filieres")
public class Filiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "nom_filiere", nullable = false, length = 100)
    private String nomFiliere;

    @Lob
    @Column(name = "description")
    private String description;

    @Transient
    private Integer nombre_etudiant;


    public void setNombre_etudiant(Integer nombre_etudiant) {
        this.nombre_etudiant = nombre_etudiant;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNomFiliere() {
        return nomFiliere;
    }

    public void setNomFiliere(String nomFiliere) {
        this.nomFiliere = nomFiliere;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNombre_etudiant() {
        return nombre_etudiant;
    }
}