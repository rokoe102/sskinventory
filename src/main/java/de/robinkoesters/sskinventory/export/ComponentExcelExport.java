package de.robinkoesters.sskinventory.export;

import de.robinkoesters.sskinventory.SSKInventory;
import de.robinkoesters.sskinventory.entity.Article;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class ComponentExcelExport {

    public static void exportAllComponents() throws Exception {
        ComponentRepository repo = new ComponentRepository();

        List<Component> components = repo.findAllComponentsForExcelExport();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Alle Komponenten");

        Row dateRow = sheet.createRow(0);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Datum: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        Font boldFont = workbook.createFont();
        boldFont.setFontHeightInPoints((short)11);
        boldFont.setFontName(HSSFFont.FONT_ARIAL);
        boldFont.setBold(true);

        Row header = sheet.createRow(2);
        Cell first = header.createCell(0);
        first.getCellStyle().setFont(boldFont);
        first.setCellValue("Bezeichnung");

        Cell second = header.createCell(1);
        second.getCellStyle().setFont(boldFont);
        second.setCellValue("Stückzahl");

        int rowCount = 4;

        for (Component c : components) {
            Row row = sheet.createRow(rowCount++);

            Cell firstCell = row.createCell(0);
            firstCell.setCellValue(c.getIdentifier());

            Cell secondCell = row.createCell(1);
            secondCell.setCellValue(c.getAmount());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        exportWorkbook(workbook);
    }

    public static void exportComponentsForArticle(Article article) throws Exception {
        ComponentRepository repo = new ComponentRepository();

        List<Component> components = repo.findComponentsAssignedToArticle(article);

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet(article.getIdentifier());

        Row dateRow = sheet.createRow(0);
        Cell dateCell = dateRow.createCell(0);
        dateCell.setCellValue("Datum: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        Font boldFont = workbook.createFont();
        boldFont.setFontHeightInPoints((short)11);
        boldFont.setFontName(HSSFFont.FONT_ARIAL);
        boldFont.setBold(true);

        Row header = sheet.createRow(2);
        Cell first = header.createCell(0);
        first.getCellStyle().setFont(boldFont);
        first.setCellValue("Bezeichnung");

        Cell second = header.createCell(1);
        second.getCellStyle().setFont(boldFont);
        second.setCellValue("Stückzahl");

        int rowCount = 4;

        for (Component c : components) {
            Row row = sheet.createRow(rowCount++);

            Cell firstCell = row.createCell(0);
            firstCell.setCellValue(c.getIdentifier());

            Cell secondCell = row.createCell(1);
            secondCell.setCellValue(c.getAmount());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        exportWorkbook(workbook);
    }

    private static void exportWorkbook(Workbook workbook) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel (.xlsx)", "*.xlsx"));
        File selectedFile = fileChooser.showSaveDialog(SSKInventory.getMainStage());

        try (FileOutputStream outputStream = new FileOutputStream(selectedFile)) {
            workbook.write(outputStream);
            System.out.println("Excel-Export von " + selectedFile.getName() + "erfolgreich");
        }
    }
}
