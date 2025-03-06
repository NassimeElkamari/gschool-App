package gschool.app.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import gschool.app.entity.Etudiant;
import gschool.app.repository.EtudiantRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportEtudiantService {

    private final EtudiantRepository etudiantRepository;

    public ExportEtudiantService(EtudiantRepository etudiantRepository) {
        this.etudiantRepository = etudiantRepository;
    }


    // Generate PDF for Etudiants
    public byte[] generateEtudiantPdf() {
        List<Etudiant> etudiants = etudiantRepository.findAll();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            document.add(new Paragraph("Liste des Étudiants").setBold().setFontSize(18));

            // Create PDF table
            Table table = new Table(5); // 6 columns: Photo, Nom, Prénom, Email, Code, Filière
            table.addHeaderCell(new Cell().add(new Paragraph("Nom")));
            table.addHeaderCell(new Cell().add(new Paragraph("Prénom")));
            table.addHeaderCell(new Cell().add(new Paragraph("Email")));
            table.addHeaderCell(new Cell().add(new Paragraph("Code")));
            table.addHeaderCell(new Cell().add(new Paragraph("Filière")));

            // Fill the table
            for (Etudiant etudiant : etudiants) {
                table.addCell(new Cell().add(new Paragraph(etudiant.getNom())));
                table.addCell(new Cell().add(new Paragraph(etudiant.getPrenom())));
                table.addCell(new Cell().add(new Paragraph(etudiant.getEmail())));
                table.addCell(new Cell().add(new Paragraph(etudiant.getCodeEtudiant())));
                table.addCell(new Cell().add(new Paragraph(etudiant.getFiliere() != null ? etudiant.getFiliere().getNomFiliere() : "Non Assignée")));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate Excel for Etudiants
    public byte[] generateEtudiantExcel() {
        List<Etudiant> etudiants = etudiantRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Étudiants");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"Photo", "Nom", "Prénom", "Email", "Code", "Filière"};
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Fill data rows
            int rowNum = 1;
            for (Etudiant etudiant : etudiants) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(etudiant.getPhoto());
                row.createCell(1).setCellValue(etudiant.getNom());
                row.createCell(2).setCellValue(etudiant.getPrenom());
                row.createCell(3).setCellValue(etudiant.getEmail());
                row.createCell(4).setCellValue(etudiant.getCodeEtudiant());
                row.createCell(5).setCellValue(etudiant.getFiliere() != null ? etudiant.getFiliere().getNomFiliere() : "Non Assignée");
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}