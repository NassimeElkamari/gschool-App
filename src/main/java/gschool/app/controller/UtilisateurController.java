package gschool.app.controller;

import gschool.app.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletResponse;
import gschool.app.entity.Utilisateur;
import gschool.app.service.ExportUtilisateurService;
import gschool.app.service.UtilisateurService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final ExportUtilisateurService exportService;
    private final UtilisateurRepository utilisateurRepository;



    public UtilisateurController(UtilisateurService utilisateurService, ExportUtilisateurService exportService, UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.exportService = exportService;
        this.utilisateurRepository = utilisateurRepository;
    }


    @GetMapping
    public String listUtilisateurs(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Utilisateur> utilisateurPage = utilisateurService.getUtilisateurs(PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("utilisateursPage", utilisateurPage);

        int totalPages = utilisateurPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            String email = ((Utilisateur) authentication.getPrincipal()).getEmail();

            Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            LocalDateTime derniereConnexion = utilisateur.getDerniereConnexion();

            model.addAttribute("userName", username);
            model.addAttribute("userEmail", email);
            model.addAttribute("derniereConnexion", derniereConnexion);
        }

        model.addAttribute("currentPage", "utilisateurs");


        return "/utilisateurs/utilisateurs";
    }

    @GetMapping("/new")
    public String createUtilisateurForm(Model model) {
        model.addAttribute("utilisateur", new Utilisateur());
        return "/utilisateurs/add_utilisateur";
    }

    @PostMapping("/save")
    public String saveUtilisateur(@ModelAttribute("utilisateur") Utilisateur utilisateur) {
        utilisateurService.saveUtilisateur(utilisateur);
        return "redirect:/utilisateurs";
    }

    @GetMapping("/edit/{id}")
    public String editUtilisateurForm(@PathVariable Integer id, Model model) {
        model.addAttribute("utilisateur", utilisateurService.getUtilisateurById(id));
        return "/utilisateurs/edit_utilisateur";
    }

    @PostMapping("/{id}")
    public String updateUtilisateur(@PathVariable Integer id,
                                    @ModelAttribute("utilisateur") Utilisateur utilisateur) {
        Utilisateur existingUtilisateur = utilisateurService.getUtilisateurById(id);
        existingUtilisateur.setNomUtilisateur(utilisateur.getNomUtilisateur());
        existingUtilisateur.setEmail(utilisateur.getEmail());
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(existingUtilisateur.getMotDePasse());
        }
        existingUtilisateur.setRole(utilisateur.getRole());
        utilisateurService.updateUtilisateur(existingUtilisateur);
        return "redirect:/utilisateurs";
    }

    @PostMapping("/{id}/delete")
    public String deleteUtilisateur(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            utilisateurService.deleteUtilisateurById(id);
            redirectAttributes.addFlashAttribute("success", "Utilisateur supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/utilisateurs";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) {
        try {
            byte[] pdfBytes = exportService.generateUtilisateurPdf();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=utilisateurs.pdf");
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) {
        try {
            byte[] excelBytes = exportService.generateUtilisateurExcel();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=utilisateurs.xlsx");
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}