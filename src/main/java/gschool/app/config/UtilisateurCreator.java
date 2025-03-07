package gschool.app.config;

import gschool.app.entity.Utilisateur;
import gschool.app.repository.UtilisateurRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(scanBasePackages = "gschool.app")
public class UtilisateurCreator {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(UtilisateurCreator.class, args);
        UtilisateurRepository utilisateurRepository = context.getBean(UtilisateurRepository.class);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail("marwa@gmail.com");
        utilisateur.setMotDePasse(encoder.encode("marwa2003"));
        utilisateur.setNomUtilisateur("marwa elkamari");
        utilisateur.setRole("admin");

        utilisateurRepository.save(utilisateur);
        System.out.println("Utilisateur enregistré avec succès !");
    }
}
