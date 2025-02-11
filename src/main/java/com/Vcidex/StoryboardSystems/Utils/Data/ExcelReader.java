package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelReader {
    private static final Logger logger = LogManager.getLogger(ExcelReader.class);
    private static final String FILE_PATH = "src/test/resources/testData.xlsx";

    /**
     * ✅ Reads all scenarios from Excel.
     */
    public static List<String> getAllScenarios() {
        List<String> scenarios = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming first sheet has scenarios
            if (sheet == null) return scenarios;

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                Cell scenarioCell = row.getCell(0);
                if (scenarioCell != null) {
                    scenarios.add(scenarioCell.getStringCellValue().trim());
                }
            }
        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", FILE_PATH, e);
        }
        return scenarios;
    }

    /**
     * ✅ Reads scenario data dynamically based on Scenario ID.
     */
    public static Map<String, Map<String, String>> getScenarioData(String scenarioID) {
        Map<String, Map<String, String>> scenarioData = new HashMap<>();
        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip headers

                    Cell scenarioCell = row.getCell(0);
                    if (scenarioCell != null && scenarioCell.getStringCellValue().trim().equals(scenarioID)) {
                        Map<String, String> pageData = new HashMap<>();
                        for (int i = 1; i < row.getLastCellNum(); i++) {
                            pageData.put(sheet.getRow(0).getCell(i).getStringCellValue(), row.getCell(i).getStringCellValue());
                        }
                        scenarioData.put(sheet.getSheetName(), pageData);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", FILE_PATH, e);
        }
        return scenarioData;
    }
}