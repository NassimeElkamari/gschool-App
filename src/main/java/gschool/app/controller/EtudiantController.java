package gschool.app.controller;

import gschool.app.entity.Utilisateur;
import gschool.app.repository.UtilisateurRepository;
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
import java.time.LocalDateTime;
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
    private final UtilisateurRepository utilisateurRepository;


    public EtudiantController(EtudiantService etudiantService, FiliereService filiereService, CloudinaryService cloudinaryService, ExportEtudiantService exportService, UtilisateurRepository utilisateurRepository) {
        this.etudiantService = etudiantService;
        this.filiereService = filiereService;
        this.cloudinaryService = cloudinaryService;
        this.exportService = exportService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @GetMapping
    public String listEtudiants(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "code", required = false) String code,
            @RequestParam(value = "sort", required = false) String sort) {

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<Etudiant> etudiantPage = etudiantService.searchEtudiants(name, email, code, sort, PageRequest.of(currentPage - 1, pageSize));

        model.addAttribute("etudiantsPage", etudiantPage);

        int totalPages = etudiantPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("code", code);
        model.addAttribute("sort", sort);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String username = authentication.getName();
            String emailUser = ((Utilisateur) authentication.getPrincipal()).getEmail();

            Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            LocalDateTime derniereConnexion = utilisateur.getDerniereConnexion();

            model.addAttribute("userName", username);
            model.addAttribute("userEmail", emailUser);
            model.addAttribute("derniereConnexion", derniereConnexion);
        }

        model.addAttribute("currentPage", "etudiants");

        return "/etudiants/etudiants";
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
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5") int size,
            Model model,
            @RequestHeader(value = "X-Requested-With", required = false) String requestedWith) {

        System.out.println("Search Parameters - Name: " + name + ", Email: " + email + ", Code: " + code + ", Sort: " + sort);

        Page<Etudiant> etudiantsPage = etudiantService.searchEtudiants(name, email, code, sort, PageRequest.of(page, size));
        model.addAttribute("etudiantsPage", etudiantsPage);

        if ("XMLHttpRequest".equals(requestedWith)) {
            return "fragments/etudiant/etudiants :: #etudiantsBody";
        }

        return "etudiants";
    }


    @GetMapping("/new")
    public String createEtudiantForm(Model model) {
        model.addAttribute("etudiant", new Etudiant());
        model.addAttribute("filieres", filiereService.getFilieres());
        return "/etudiants/add_etudiant";
    }

    @GetMapping("/edit/{id}")
    public String editEtudiantForm(@PathVariable Integer id, Model model) {
        Etudiant etudiant = etudiantService.getEtudiantById(id);

        model.addAttribute("etudiant", etudiant);
        model.addAttribute("filieres", filiereService.getFilieres());
        return "/etudiants/edit_etudiant";
    }


    @PostMapping("/{id}/delete")
    public String deleteEtudiant(@PathVariable Integer id) {
        etudiantService.deleteEtudiantById(id);
        return "redirect:/etudiants";
    }

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
