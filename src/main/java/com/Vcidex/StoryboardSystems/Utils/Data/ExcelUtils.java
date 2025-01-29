package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.poi.ss.usermodel.*;

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
                if (row.getCell(0) != null && row.getCell(0).getStringCellValue().equals(scenarioID)) {
                    Map<String, String> rowData = new HashMap<>();
                    for (int j = 0; j < row.getLastCellNum(); j++) {
                        Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        rowData.put(sheet.getRow(0).getCell(j).getStringCellValue(), getCellValue(cell));  // Using getCellValue here
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

    // ✅ Add the missing getCellValue method here
    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
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
