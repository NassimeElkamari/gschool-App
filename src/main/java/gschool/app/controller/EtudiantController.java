package gschool.app.controller;

import gschool.app.entity.Utilisateur;
import jakarta.servlet.http.HttpServletResponse;
import gschool.app.entity.Etudiant;
import gschool.app.entity.Filiere;
import gschool.app.service.CloudinaryService;
import gschool.app.service.EtudiantService;
import gschool.app.service.ExportEtudiantService;
import gschool.app.service.FiliereService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@RequestMapping("/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;
    private final FiliereService filiereService;
    private final CloudinaryService cloudinaryService;
    private final ExportEtudiantService exportService;


    public EtudiantController(EtudiantService etudiantService, FiliereService filiereService, CloudinaryService cloudinaryService, ExportEtudiantService exportService) {
        this.etudiantService = etudiantService;
        this.filiereService = filiereService;
        this.cloudinaryService = cloudinaryService;
        this.exportService = exportService;
    }

    @GetMapping
    public String listEtudiants(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        // Set default page size (2 rows per page)
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);

        // Fetch paginated data
        Page<Etudiant> etudiantPage = etudiantService.getEtudiants(PageRequest.of(currentPage - 1, pageSize));

        // Add data to the model
        model.addAttribute("etudiantsPage", etudiantPage);

        // Calculate total pages for pagination links
        int totalPages = etudiantPage.getTotalPages();
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

            // Add user info to the model
            model.addAttribute("userName", username);  // This is the user's name now
            model.addAttribute("userEmail", email);
        }

        return "etudiants"; // Return the view name
    }



    @PostMapping("/save")
    public String saveEtudiant(@ModelAttribute Etudiant etudiant,
                               @RequestParam("filiere") Integer filiereId,
                               @RequestParam("file") MultipartFile file) throws IOException {
        Filiere filiere = filiereService.getFiliereById(filiereId);
        etudiant.setFiliere(filiere);

        if (!file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file);
            etudiant.setPhoto(imageUrl);
        }

        etudiantService.saveEtudiant(etudiant);
        return "redirect:/etudiants";
    }

    @PostMapping("/{id}")
    public String updateEtudiant(@PathVariable Integer id,
                                 @ModelAttribute("etudiant") Etudiant etudiant,
                                 @RequestParam("filiere") Integer filiereId,
                                 @RequestParam("file") MultipartFile file) throws IOException {
        Etudiant existingEtudiant = etudiantService.getEtudiantById(id);
        Filiere filiere = filiereService.getFiliereById(filiereId);

        existingEtudiant.setNom(etudiant.getNom());
        existingEtudiant.setPrenom(etudiant.getPrenom());
        existingEtudiant.setEmail(etudiant.getEmail());
        existingEtudiant.setDateNaissance(etudiant.getDateNaissance());
        existingEtudiant.setCodeEtudiant(etudiant.getCodeEtudiant());
        existingEtudiant.setFiliere(filiere);

        if (!file.isEmpty()) {
            String imageUrl = cloudinaryService.uploadFile(file);
            existingEtudiant.setPhoto(imageUrl);
        }

        etudiantService.updateEtudiant(existingEtudiant);
        return "redirect:/etudiants";
    }

    @GetMapping("/search")
    public String searchEtudiants(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "sort", required = false) String sort,
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        List<Etudiant> etudiants = etudiantService.searchEtudiants(name, email, code, sort);
        model.addAttribute("etudiants", etudiants);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "fragments/etudiant/etudiants :: #etudiantsBody";
        }

        return "etudiants";
    }


    @GetMapping("/new")
    public String createEtudiantForm(Model model) {
        model.addAttribute("etudiant", new Etudiant()); // Add an empty Etudiant object
        model.addAttribute("filieres", filiereService.getFilieres()); // Add filieres for the dropdown
        return "add_etudiant"; // Return the full view name
    }

    @GetMapping("/edit/{id}")
    public String editEtudiantForm(@PathVariable Integer id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id); // Fetch the student by ID
        model.addAttribute("etudiant", etudiant); // Add the student to the model
        model.addAttribute("filieres", filiereService.getFilieres()); // Add filieres for the dropdown
        return "edit_etudiant"; // Return the full view name
    }

    @GetMapping("/{id}/delete")
    public String confirmDelete(@PathVariable Integer id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);
        model.addAttribute("etudiant", etudiant);
        return "fragments/etudiant/delete_etudiant";
    }

    @PostMapping("/{id}/delete")
    public String deleteEtudiant(@PathVariable Integer id) {
        etudiantService.deleteEtudiantById(id);
        return "redirect:/etudiants";
    }

    // Export PDF for Etudiants
    @GetMapping("/export/pdf")
    public void exportPdf(HttpServletResponse response) {
        try {
            byte[] pdfBytes = exportService.generateEtudiantPdf();

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=etudiants.pdf");
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Export Excel for Etudiants
    @GetMapping("/export/excel")
    public void exportExcel(HttpServletResponse response) {
        try {
            byte[] excelBytes = exportService.generateEtudiantExcel();

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=etudiants.xlsx");
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
