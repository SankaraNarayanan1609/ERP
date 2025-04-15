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
    private static final String DEFAULT_FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/PurchaseTestData.xlsx";

    /**
     * ✅ Reads **specific scenario data** dynamically and handles missing FilePath and Terms.
     */
    public static Map<String, String> getScenarioData(String scenarioID, String customFilePath) {
        String filePath = (customFilePath != null && !customFilePath.isEmpty()) ? customFilePath : DEFAULT_FILE_PATH;
        Map<String, String> scenarioData = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            boolean scenarioFound = false;

            for (Sheet sheet : workbook) {
                if (sheet.getPhysicalNumberOfRows() == 0) continue; // Skip empty sheets

                int scenarioColumnIndex = findScenarioColumn(sheet, scenarioID);
                if (scenarioColumnIndex == -1) continue;

                scenarioFound = true;
                logger.info("✅ Scenario '{}' found in sheet '{}'", scenarioID, sheet.getSheetName());

                // ✅ Read data from the identified scenario column
                readScenarioData(sheet, scenarioColumnIndex, scenarioData);
            }

            if (!scenarioFound) {
                logger.warn("⚠️ Scenario '{}' not found in any sheet. Returning empty data.", scenarioID);
            }

            validateAndSetDefaults(scenarioData, scenarioID);

            // ✅ Log the fetched data
            logger.info("Fetched Data for Scenario '{}': {}", scenarioID, scenarioData);

        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", filePath, e);
        }
        return scenarioData;
    }

    /**
     * ✅ Finds the column index of the scenario.
     */
    private static int findScenarioColumn(Sheet sheet, String scenarioID) {
        Row headerRow = null;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) != null) {
                headerRow = sheet.getRow(i);
                break;
            }
        }

        if (headerRow == null) return -1;

        for (Cell cell : headerRow) {
            String cellValue = cell.getStringCellValue().trim();
            if (cellValue.equalsIgnoreCase(scenarioID)) {
                int scenarioColumnIndex = cell.getColumnIndex();
                logger.info("Scenario Column Index Found: {} in sheet '{}'", scenarioColumnIndex, sheet.getSheetName());
                return scenarioColumnIndex;
            }
        }
        logger.warn("⚠️ Scenario '{}' not found in sheet '{}'", scenarioID, sheet.getSheetName());
        return -1;
    }

    /**
     * ✅ Reads data for the given scenario column, skipping label "PO_Details".
     */
    private static void readScenarioData(Sheet sheet, int scenarioColumnIndex, Map<String, String> scenarioData) {
        for (Row row : sheet) {
            logger.info("🧩 Available keys in scenarioData: {}", scenarioData.keySet());

            if (row.getRowNum() == 0) continue; // Skip header

            Cell keyCell = row.getCell(0);  // First column for keys
            Cell valueCell = row.getCell(scenarioColumnIndex);  // Scenario column for values

            if (keyCell != null && valueCell != null) {
                String key = keyCell.getStringCellValue().trim();
                String value = getCellValueAsString(valueCell).trim();

                // ✅ Skip the first row label "PO_Details"
                if (!key.equalsIgnoreCase("PO_Details")) {
                    scenarioData.put(key, value);
                }
            }
        }
    }

    /**
     * ✅ Converts any cell type to String safely.
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return ""; // Handle null case explicitly
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // Or format with SimpleDateFormat
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula().trim();
            case BLANK:
                return "";
            default:
                return "N/A";
        }
    }

    /**
     * ✅ Validate scenario data and set defaults if necessary.
     */
    private static void validateAndSetDefaults(Map<String, String> scenarioData, String scenarioID) {
        if (!scenarioData.containsKey("FilePath") || scenarioData.get("FilePath").isEmpty()) {
            logger.warn("⚠️ 'FilePath' not found or empty for scenario '{}'. Using default path.", scenarioID);
            scenarioData.put("FilePath", "C:\\Users\\SankaraNarayanan\\IdeaProjects\\StoryboardsSystems\\src\\test\\resources\\PurchaseTestData.xlsx");
        }
        if (!scenarioData.containsKey("Terms") || scenarioData.get("Terms").isEmpty()) {
            logger.warn("⚠️ 'Terms' not found or empty for scenario '{}'. Using default terms.", scenarioID);
            scenarioData.put("Terms", "Standard Terms");
        }
    }
}