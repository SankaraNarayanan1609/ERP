package com.Vcidex.StoryboardSystems.Utils.Data;

import com.Vcidex.StoryboardSystems.Purchase.POJO.PurchaseOrderData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExcelReader {

    private static final Logger logger = LogManager.getLogger(ExcelReader.class);
    private static final String DEFAULT_FILE_PATH = System.getProperty("user.dir") + "/src/test/resources/PurchaseTestData.xlsx";

    /**
     * Reads Excel data and maps each row to a PurchaseOrderData object.
     *
     * @param customFilePath Optional custom file path; uses default if null or empty.
     * @return List of PurchaseOrderData objects.
     */
    public static List<PurchaseOrderData> readPurchaseOrderData(String customFilePath) {
        String filePath = (customFilePath != null && !customFilePath.isEmpty()) ? customFilePath : DEFAULT_FILE_PATH;
        List<PurchaseOrderData> purchaseOrderList = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            Iterator<Row> rowIterator = sheet.iterator();

            if (!rowIterator.hasNext()) {
                logger.warn("⚠️ The sheet is empty.");
                return purchaseOrderList;
            }

            // Read header row
            Row headerRow = rowIterator.next();
            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue().trim());
            }

            // Process data rows
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                PurchaseOrderData pod = new PurchaseOrderData();

                for (int i = 0; i < headers.size(); i++) {
                    Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    String fieldName = headers.get(i);
                    setFieldValue(pod, fieldName, cell);
                }

                purchaseOrderList.add(pod);
            }

        } catch (IOException e) {
            logger.error("❌ Error reading Excel file: {}", filePath, e);
        }

        return purchaseOrderList;
    }
    /**
     * Retrieves a specific PurchaseOrderData object based on the scenario ID.
     *
     * @param scenarioID The scenario ID to match.
     * @param filePath   The path to the Excel file.
     * @return The matching PurchaseOrderData object, or null if not found.
     */
    public static PurchaseOrderData getScenarioAsPOJO(String scenarioID, String filePath) {
        List<PurchaseOrderData> allData = readPurchaseOrderData(filePath);
        for (PurchaseOrderData pod : allData) {
            if (pod.getScenarioID() != null && pod.getScenarioID().equalsIgnoreCase(scenarioID)) {
                return pod;
            }
        }
        logger.warn("⚠️ Scenario ID '{}' not found in data file: {}", scenarioID, filePath);
        return null;
    }
    /**
     * Sets the value of a field in the PurchaseOrderData object using reflection.
     *
     * @param pod       The PurchaseOrderData object.
     * @param fieldName The name of the field to set.
     * @param cell      The Excel cell containing the value.
     */

    private static void setFieldValue(PurchaseOrderData pod, String fieldName, Cell cell) {
        try {
            Field field = PurchaseOrderData.class.getDeclaredField(convertToCamelCase(fieldName));
            field.setAccessible(true);

            switch (cell.getCellType()) {
                case STRING:
                    if (field.getType() == String.class) {
                        field.set(pod, cell.getStringCellValue().trim());
                    } else if (field.getType() == Date.class) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        field.set(pod, sdf.parse(cell.getStringCellValue().trim()));
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        field.set(pod, Double.parseDouble(cell.getStringCellValue().trim()));
                    }
                    break;
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell) && field.getType() == Date.class) {
                        field.set(pod, cell.getDateCellValue());
                    } else if (field.getType() == double.class || field.getType() == Double.class) {
                        field.set(pod, cell.getNumericCellValue());
                    } else if (field.getType() == String.class) {
                        field.set(pod, String.valueOf(cell.getNumericCellValue()));
                    }
                    break;
                case BOOLEAN:
                    if (field.getType() == boolean.class || field.getType() == Boolean.class) {
                        field.set(pod, cell.getBooleanCellValue());
                    } else if (field.getType() == String.class) {
                        field.set(pod, String.valueOf(cell.getBooleanCellValue()));
                    }
                    break;
                case BLANK:
                    field.set(pod, null);
                    break;
                default:
                    field.set(pod, null);
                    break;
            }

        } catch (NoSuchFieldException e) {
            logger.warn("⚠️ No such field: {}", fieldName);
        } catch (Exception e) {
            logger.error("❌ Error setting field value for {}: {}", fieldName, e.getMessage());
        }
    }

    /**
     * Converts a string to camelCase to match Java field naming conventions.
     *
     * @param input The input string.
     * @return The camelCase version of the string.
     */
    private static String convertToCamelCase(String input) {
        StringBuilder result = new StringBuilder();
        boolean nextIsUpper = false;
        for (char c : input.toCharArray()) {
            if (c == '_' || c == ' ') {
                nextIsUpper = true;
            } else {
                if (nextIsUpper) {
                    result.append(Character.toUpperCase(c));
                    nextIsUpper = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();

    }
}