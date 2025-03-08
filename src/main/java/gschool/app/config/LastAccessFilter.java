package gschool.app.config;

import gschool.app.entity.Utilisateur;
import gschool.app.repository.UtilisateurRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class LastAccessFilter extends OncePerRequestFilter {

    private final UtilisateurRepository utilisateurRepository;

    public LastAccessFilter(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();

            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByNomUtilisateur(username);

            if (utilisateurOpt.isPresent()) {
                Utilisateur utilisateur = utilisateurOpt.get();

                utilisateur.setDerniereConnexion(LocalDateTime.now());

                utilisateurRepository.save(utilisateur);
            }
        }

        filterChain.doFilter(request, response);
    }
}