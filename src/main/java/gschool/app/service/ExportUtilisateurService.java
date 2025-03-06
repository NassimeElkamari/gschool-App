package gschool.app.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import gschool.app.entity.Utilisateur;
import gschool.app.repository.UtilisateurRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportUtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public ExportUtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }


    // Generate PDF for Utilisateurs
    public byte[] generateUtilisateurPdf() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            document.add(new Paragraph("Liste des Utilisateurs").setBold().setFontSize(18));

            // Create PDF table
            Table table = new Table(4); // 4 columns: ID, Nom, Email, Rôle
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Nom")));
            table.addHeaderCell(new Cell().add(new Paragraph("Email")));
            table.addHeaderCell(new Cell().add(new Paragraph("Rôle")));

            // Fill the table
            for (Utilisateur utilisateur : utilisateurs) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(utilisateur.getId()))));
                table.addCell(new Cell().add(new Paragraph(utilisateur.getNomUtilisateur())));
                table.addCell(new Cell().add(new Paragraph(utilisateur.getEmail())));
                table.addCell(new Cell().add(new Paragraph(utilisateur.getRole())));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate Excel for Utilisateurs
    public byte[] generateUtilisateurExcel() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Utilisateurs");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Email", "Rôle"};
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
            for (Utilisateur utilisateur : utilisateurs) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(utilisateur.getId());
                row.createCell(1).setCellValue(utilisateur.getNomUtilisateur());
                row.createCell(2).setCellValue(utilisateur.getEmail());
                row.createCell(3).setCellValue(utilisateur.getRole());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}