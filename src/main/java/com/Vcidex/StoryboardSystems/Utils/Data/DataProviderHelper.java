package com.Vcidex.StoryboardSystems.Utils.Data;

import org.testng.annotations.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

public class DataProviderHelper {

    private static final Logger logger = LoggerFactory.getLogger(DataProviderHelper.class);

    @DataProvider(name = "ExcelDataProvider")
    public Iterator<Object[]> getDataFromExcel() {
        String filePath = "src/test/resources/testData.xlsx";
        if (!Paths.get(filePath).toFile().exists()) {
            logger.error("Excel file not found at path: {}", filePath);
            throw new RuntimeException("Excel file not found at path: " + filePath);
        }

        try (FileInputStream fis = new FileInputStream(filePath)) {
            ExcelReader excelReader = new ExcelReader(fis);
            logger.info("Data loaded from Excel file: {}", filePath);
            return (Iterator<Object[]>) excelReader.getExcelData("Sheet1");
        } catch (IOException e) {
            logger.error("Error opening Excel file: {}", filePath, e);
            throw new RuntimeException("Error opening Excel file: " + filePath, e);
        }
    }
}
