package gschool.app.controller;

import gschool.app.entity.Utilisateur;
import gschool.app.repository.UtilisateurRepository;
import jakarta.servlet.http.HttpServletResponse;
import gschool.app.entity.Filiere;
import gschool.app.service.ExportFiliereService;
import gschool.app.service.FiliereService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/filieres")
public class FiliereController {

    private final FiliereService filiereService;
    private final ExportFiliereService exportService;
    private final UtilisateurRepository utilisateurRepository;


    public FiliereController(FiliereService filiereService, ExportFiliereService exportService, UtilisateurRepository utilisateurRepository) {
        this.filiereService = filiereService;
        this.exportService = exportService;


        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping
    public String listFilieres(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        // Set default page size (2 rows per page)
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        // Fetch paginated data
        Page<Filiere> filierePage = filiereService.getFilieres(PageRequest.of(currentPage - 1, pageSize));

        // Add data to the model
        model.addAttribute("filieresPage", filierePage);

        // Calculate total pages for pagination links
        int totalPages = filierePage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        // Add the number of students for each Filiere
        List<Filiere> filieres = filierePage.getContent();
        for (Filiere filiere : filieres) {
            Integer studentCount = filiereService.getNumberOfStudentsInFiliere(filiere.getId());
            filiere.setNombre_etudiant(studentCount);  // Assuming you add a `setStudentCount` method in the Filiere class
        }
        model.addAttribute("filieres", filieres);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();  // This now returns nomUtilisateur
            String email = ((Utilisateur) authentication.getPrincipal()).getEmail();

            // Fetch the current user's last connection time
            Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            LocalDateTime derniereConnexion = utilisateur.getDerniereConnexion();

            // Add user info to the model
            model.addAttribute("userName", username);  // This is the user's name now
            model.addAttribute("userEmail", email);
            model.addAttribute("derniereConnexion", derniereConnexion); // Add last connection time
        }

        model.addAttribute("currentPage", "filieres");

        return "filieres"; // Return the view name
    }


    @GetMapping("/new")
    public String createFiliereForm(Model model) {
        model.addAttribute("filiere", new Filiere());
        return "add_filiere"; // Return the full view name
    }

    @PostMapping("/save")
    public String saveFiliere(@ModelAttribute("filiere") Filiere filiere) {
        filiereService.saveFiliere(filiere);
        return "redirect:/filieres";
    }

    @GetMapping("/edit/{id}")
    public String editFiliereForm(@PathVariable Integer id, Model model) {
        model.addAttribute("filiere", filiereService.getFiliereById(id));
        return "edit_filiere"; // Return the full view name
    }

    @PostMapping("/{id}")
    public String updateFiliere(@PathVariable Integer id,
                                @ModelAttribute("filiere") Filiere filiere) {
        Filiere existingFiliere = filiereService.getFiliereById(id);
        existingFiliere.setNomFiliere(filiere.getNomFiliere());
        existingFiliere.setDescription(filiere.getDescription());
        filiereService.updateFiliere(existingFiliere);
        return "redirect:/filieres";
    }

    @PostMapping("/{id}/delete")
    public String deleteFiliere(@PathVariable Integer id) {
        filiereService.deleteFiliereById(id);
        return "redirect:/filieres";
    }

    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) {
        try {
            byte[] pdfBytes = exportService.generateFilierePdf();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=filieres.pdf");
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Export Excel
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) {
        try {
            byte[] excelBytes = exportService.generateFiliereExcel();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=filieres.xlsx");
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
