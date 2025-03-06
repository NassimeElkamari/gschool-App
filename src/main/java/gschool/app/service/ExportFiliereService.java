package gschool.app.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import gschool.app.entity.Filiere;
import gschool.app.repository.FiliereRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class ExportFiliereService {

    private final FiliereRepository filiereRepository;



    public ExportFiliereService(FiliereRepository filiereRepository) {
        this.filiereRepository = filiereRepository;
    }

    // Generate PDF
    public byte[] generateFilierePdf() {
        List<Filiere> filieres = filiereRepository.findAll();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Add title
            document.add(new Paragraph("Liste des Filières").setBold().setFontSize(18));

            // Create PDF table
            Table table = new Table(3);
            table.addHeaderCell(new Cell().add(new Paragraph("ID")));
            table.addHeaderCell(new Cell().add(new Paragraph("Nom")));
            table.addHeaderCell(new Cell().add(new Paragraph("Description")));

            // Fill the table
            for (Filiere filiere : filieres) {
                table.addCell(new Cell().add(new Paragraph(String.valueOf(filiere.getId()))));
                table.addCell(new Cell().add(new Paragraph(filiere.getNomFiliere())));
                table.addCell(new Cell().add(new Paragraph(filiere.getDescription())));
            }

            document.add(table);
            document.close();
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Generate Excel
    public byte[] generateFiliereExcel() {
        List<Filiere> filieres = filiereRepository.findAll();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Filières");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Nom", "Description"};
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
            for (Filiere filiere : filieres) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(filiere.getId());
                row.createCell(1).setCellValue(filiere.getNomFiliere());
                row.createCell(2).setCellValue(filiere.getDescription());
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}