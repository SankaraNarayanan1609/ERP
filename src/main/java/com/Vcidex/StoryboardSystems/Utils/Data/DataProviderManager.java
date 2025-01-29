package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class DataProviderManager {
    private static final Logger logger = LoggerFactory.getLogger(DataProviderManager.class);

    /**
     * Reads Excel data and returns it in a list of maps, where each map represents a row.
     *
     * @param filePath  Path to the Excel file.
     * @param sheetName Name of the sheet to read from.
     * @return List of maps containing row data.
     */
    public static List<Map<String, String>> readExcelData(String filePath, String sheetName) {
        List<Map<String, String>> data = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row is missing in the Excel file.");
            }

            List<String> headers = new ArrayList<>();
            for (Cell headerCell : headerRow) {
                headers.add(headerCell.toString().trim());
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < headers.size(); j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    rowData.put(headers.get(j), getCellValue(cell));
                }
                data.add(rowData);
            }

        } catch (IOException e) {
            logger.error("Failed to read data from file: {} and sheet: {}", filePath, sheetName, e);
            throw new RuntimeException("Error reading Excel file.", e);
        }

        return data;
    }

    /**
     * Writes data into an Excel file.
     *
     * @param filePath  Path to the Excel file.
     * @param sheetName Name of the sheet where data should be written.
     * @param data      List of maps, each representing a row.
     */
    public static void writeExcelData(String filePath, String sheetName, List<Map<String, String>> data) {
        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(filePath)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            } else {
                sheet = workbook.getSheetAt(0);
            }

            // Write headers if the sheet is empty
            if (sheet.getLastRowNum() == 0) {
                Row headerRow = sheet.createRow(0);
                int headerCellIndex = 0;
                for (String key : data.get(0).keySet()) {
                    headerRow.createCell(headerCellIndex++).setCellValue(key);
                }
            }

            // Write data rows
            int rowIndex = sheet.getLastRowNum() + 1;
            for (Map<String, String> rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                int cellIndex = 0;
                for (String value : rowData.values()) {
                    row.createCell(cellIndex++).setCellValue(value);
                }
            }

            workbook.write(fos);
            logger.info("Data written successfully to {} (Sheet: {})", filePath, sheetName);
        } catch (IOException e) {
            logger.error("Failed to write data to file: {} and sheet: {}", filePath, sheetName, e);
            throw new RuntimeException("Error writing Excel file.", e);
        }
    }

    /**
     * Converts list of maps to TestNG DataProvider format (Object[][]).
     *
     * @param data List of maps containing Excel row data.
     * @return Object[][] for DataProvider.
     */
    private static Object[][] convertToDataProviderFormat(List<Map<String, String>> data) {
        return data.stream().map(row -> new Object[]{row}).toArray(Object[][]::new);
    }

    /**
     * Fetches test data for TestNG using an Excel file.
     *
     * @return Object[][] test data.
     */
    @DataProvider(name = "ExcelDataProvider")
    public static Object[][] getExcelData() {
        String filePath = "src/test/resources/testData.xlsx";
        List<Map<String, String>> data = readExcelData(filePath, "Sheet1");
        return convertToDataProviderFormat(data);
    }

    /**
     * Gets a cell's value as a String.
     *
     * @param cell The Excel cell.
     * @return The cell value as a string.
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
            default:
                return "";
        }
    }
}