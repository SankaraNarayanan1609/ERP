package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class DataProviderManager {
    private static final Logger logger = LogManager.getLogger(DataProviderManager.class);
    private static final String FILE_PATH = "src/test/resources/testData.xlsx";
    private static final Map<String, Map<String, Map<String, String>>> cachedData = new HashMap<>();

    public static Map<String, String> readPageData(String sheetName, String scenarioID) {
        if (!cachedData.containsKey(sheetName)) {
            cachedData.put(sheetName, readExcelSheet(sheetName));
        }
        return cachedData.getOrDefault(sheetName, Collections.emptyMap()).getOrDefault(scenarioID, new HashMap<>());
    }

    private static Map<String, Map<String, String>> readExcelSheet(String sheetName) {
        Map<String, Map<String, String>> sheetData = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(FILE_PATH);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) return sheetData;

            Row headerRow = sheet.getRow(0);
            if (headerRow == null) return sheetData;

            for (int j = 1; j < headerRow.getLastCellNum(); j++) {
                String scenarioID = headerRow.getCell(j).getStringCellValue().trim();
                if (!scenarioID.isEmpty()) {
                    sheetData.put(scenarioID, new HashMap<>());
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String key = row.getCell(0).getStringCellValue().trim();
                if (key.isEmpty()) continue;

                for (int j = 1; j < row.getLastCellNum(); j++) {
                    String scenarioID = headerRow.getCell(j).getStringCellValue().trim();
                    String value = row.getCell(j).getStringCellValue().trim();
                    if (!scenarioID.isEmpty()) {
                        sheetData.get(scenarioID).put(key, value);
                    }
                }
            }
        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", FILE_PATH, e);
        }
        return sheetData;
    }

    @DataProvider(name = "ExcelDataProvider")
    public static Object[][] getExcelData() {
        String scenarioID = "Scenario1";
        List<String> requiredPages = Arrays.asList("DirectPO", "Invoice");

        Map<String, Map<String, String>> scenarioData = new HashMap<>();
        for (String page : requiredPages) {
            scenarioData.put(page, readPageData(page, scenarioID));
        }

        return new Object[][]{{scenarioData}};
    }
}