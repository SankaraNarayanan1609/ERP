package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {

    private final FileInputStream fis;

    public ExcelReader(FileInputStream fis) {
        this.fis = fis;
    }

    /**
     * Reads data from an Excel sheet and returns it as a list of maps.
     * Each map represents a row where the keys are column headers and values are cell data.
     *
     * @param sheetName the sheet name to read from
     * @return List of maps containing the row data
     */
    public List<Map<String, String>> getExcelData(String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in the Excel file.");
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row is missing in the Excel file.");
            }

            // Extract headers to use as keys for the map
            List<String> headers = new ArrayList<>();
            for (Cell headerCell : headerRow) {
                headers.add(headerCell.toString().trim());
            }

            // Process each row after the header row
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String cellValue = getCellValue(cell);
                    rowData.put(headers.get(j), cellValue);
                }
                data.add(rowData);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading Excel file.", e);
        }
        return data;
    }

    /**
     * Gets the value from a cell, handling different types.
     *
     * @param cell the cell to read
     * @return the cell's value as a string
     */
    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // You can format the date string if needed
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
