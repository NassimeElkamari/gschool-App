package net.javaguides.sms.controller;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletResponse;
import net.javaguides.sms.entity.Filiere;
import net.javaguides.sms.service.ExportFiliereService;
import net.javaguides.sms.service.FiliereService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.Document;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Controller
@RequestMapping("/filieres")
public class FiliereController {

    private final FiliereService filiereService;
    private final ExportFiliereService exportService;


    public FiliereController(FiliereService filiereService, ExportFiliereService exportService) {
        this.filiereService = filiereService;
        this.exportService = exportService;
    }

    @GetMapping
    public String listFilieres(
            Model model,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size) {

        // Set default page size (2 rows per page)
        int currentPage = page.orElse(1);
        int pageSize = size.orElse(2);

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