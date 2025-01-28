package com.Vcidex.StoryboardSystems.Utils.Data;

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataProviderUtils {

    private static final Logger logger = LoggerFactory.getLogger(DataProviderUtils.class);

    public Object[][] getData(String filePath, String sheetName) {
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet not found: " + sheetName);
            }

            List<Object[]> data = new ArrayList<>();

            // Iterating through rows
            for (Row row : sheet) {
                int lastCellNum = row.getLastCellNum();
                Object[] rowData = new Object[lastCellNum];

                for (int i = 0; i < lastCellNum; i++) {
                    rowData[i] = (row.getCell(i) != null) ? row.getCell(i).toString().trim() : null; // Handle nulls explicitly
                }
                data.add(rowData);
            }

            logger.info("Data fetched successfully from file: {} and sheet: {}. Total rows: {}", filePath, sheetName, data.size());
            return data.toArray(new Object[0][]);

        } catch (IOException e) {
            logger.error("Failed to read data from file: {} and sheet: {}", filePath, sheetName, e);
            throw new RuntimeException("Error reading data from Excel", e); // Consider a custom exception
        }
    }
}