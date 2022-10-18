package de.robinkoesters.sskinventory.export;

import de.robinkoesters.sskinventory.SSKInventory;
import de.robinkoesters.sskinventory.entity.Component;
import de.robinkoesters.sskinventory.repository.ComponentRepository;
import javafx.stage.FileChooser;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ComponentExcelExport {

    public static void exportComponents() throws Exception {
        ComponentRepository repo = new ComponentRepository();

        List<Component> components = repo.findAllComponentsForExcelExport();

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Alle Komponenten");

        Font boldFont = workbook.createFont();
        boldFont.setFontHeightInPoints((short)11);
        boldFont.setFontName(HSSFFont.FONT_ARIAL);
        boldFont.setBold(true);

        Row header = sheet.createRow(0);
        Cell first = header.createCell(0);
        first.getCellStyle().setFont(boldFont);
        first.setCellValue("Bezeichnung");

        Cell second = header.createCell(1);
        second.getCellStyle().setFont(boldFont);
        second.setCellValue("Stückzahl");

        int rowCount = 1;

        for (Component c : components) {
            Row row = sheet.createRow(rowCount++);

            Cell firstCell = row.createCell(0);
            firstCell.setCellValue(c.getIdentifier());

            Cell secondCell = row.createCell(1);
            secondCell.setCellValue(c.getAmount());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel (.xlsx)", "*.xlsx"));
        File selectedFile = fileChooser.showSaveDialog(SSKInventory.getMainStage());

        try (FileOutputStream outputStream = new FileOutputStream(selectedFile)) {
            workbook.write(outputStream);
            System.out.println("Excel-Export von" + selectedFile.getName() + "erfolgreich");
        }
    }
}
