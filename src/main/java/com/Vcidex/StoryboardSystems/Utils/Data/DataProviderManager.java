package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import java.io.*;
import java.util.*;

public class DataProviderManager {
    private static final Logger logger = LogManager.getLogger(DataProviderManager.class);

    public static Map<String, String> readPageData(String filePath, String sheetName, String scenarioID) {
        Map<String, String> data = new HashMap<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) return data;

            Row headerRow = sheet.getRow(0);
            int scenarioIndex = -1;
            for (int i = 1; i < headerRow.getLastCellNum(); i++) {
                if (headerRow.getCell(i).getStringCellValue().trim().equalsIgnoreCase(scenarioID)) {
                    scenarioIndex = i;
                    break;
                }
            }

            if (scenarioIndex == -1) return data;

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                data.put(row.getCell(0).getStringCellValue().trim(), row.getCell(scenarioIndex).getStringCellValue().trim());
            }

        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", filePath, e);
        }
        return data;
    }

    @DataProvider(name = "ExcelDataProvider")
    public static Object[][] getExcelData() {
        String filePath = "src/test/resources/testData.xlsx";
        String scenarioID = "Scenario1";
        List<String> requiredPages = Arrays.asList("DirectPO", "Invoice");

        Map<String, Map<String, String>> scenarioData = new HashMap<>();
        for (String page : requiredPages) {
            scenarioData.put(page, readPageData(filePath, page, scenarioID));
        }

        return new Object[][]{{scenarioData}};
    }
}