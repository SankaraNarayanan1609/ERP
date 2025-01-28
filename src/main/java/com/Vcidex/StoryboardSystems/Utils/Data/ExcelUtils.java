package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ExcelUtils {

    // Method to fetch data for a specific page and scenario ID
    public static Object[][] getPageData(String filePath, String pageName, String scenarioID) {
        List<Map<String, String>> data = readExcelData(filePath, pageName, scenarioID);
        return convertToDataProviderFormat(data);
    }

    // Method to fetch data for a specific flow scenario ID
    public static Object[][] getFlowData(String filePath, String flowScenarioID) {
        List<Map<String, String>> data = readExcelData(filePath, null, flowScenarioID);
        return convertToDataProviderFormat(data);
    }

    // Read data from Excel
    private static List<Map<String, String>> readExcelData(String filePath, String sheetName, String scenarioID) {
        List<Map<String, String>> data = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook workbook = WorkbookFactory.create(fis);
            Sheet sheet = (sheetName != null) ? workbook.getSheet(sheetName) : workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getCell(0).getStringCellValue().equals(scenarioID)) {
                    Map<String, String> rowData = new HashMap<>();
                    for (Cell cell : row) {
                        rowData.put(sheet.getRow(0).getCell(cell.getColumnIndex()).getStringCellValue(), cell.toString().trim());
                    }
                    data.add(rowData);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to read data from Excel", e);
        }
        return data;
    }

    // Convert list of maps to a DataProvider-compatible 2D array
    private static Object[][] convertToDataProviderFormat(List<Map<String, String>> data) {
        Object[][] result = new Object[data.size()][1];
        for (int i = 0; i < data.size(); i++) {
            result[i][0] = data.get(i);
        }
        return result;
    }
}
