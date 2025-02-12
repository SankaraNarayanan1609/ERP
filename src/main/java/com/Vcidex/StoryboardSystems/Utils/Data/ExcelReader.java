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
    private static final String FILE_PATH = "C:\\Users\\SankaraNarayanan\\IdeaProjects\\StoryboardsSystems\\src\\test\\resources\\PurchaseTestData.xlsx";

    /**
     * ✅ Reads **specific scenario data** dynamically.
     */
    public static Map<String, String> getScenarioData(String scenarioID) {
        Map<String, String> scenarioData = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            for (Sheet sheet : workbook) {
                int scenarioColumnIndex = -1;

                // ✅ Find scenario column in header row (fixes typo issue)
                Row headerRow = sheet.getRow(0);
                if (headerRow != null) {
                    for (Cell cell : headerRow) {
                        String cellValue = cell.getStringCellValue().trim();
                        if (cellValue.equalsIgnoreCase(scenarioID) || cellValue.contains(scenarioID)) {
                            scenarioColumnIndex = cell.getColumnIndex();
                            break;
                        }
                    }
                }

                if (scenarioColumnIndex == -1) {
                    logger.warn("⚠️ Scenario '{}' not found in sheet '{}'", scenarioID, sheet.getSheetName());
                    continue;
                }

                // ✅ Read scenario data from this column
                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip headers

                    Cell keyCell = row.getCell(0);  // First column has the keys
                    Cell valueCell = row.getCell(scenarioColumnIndex);  // Value from identified column

                    if (keyCell != null && valueCell != null) {
                        String key = keyCell.getStringCellValue().trim();
                        String value = getCellValueAsString(valueCell);
                        scenarioData.put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", FILE_PATH, e);
        }
        return scenarioData;
    }

    /**
     * ✅ Converts any cell type to String safely.
     */
    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "UNKNOWN";
        }
    }
}