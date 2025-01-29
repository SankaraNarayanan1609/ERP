package com.Vcidex.StoryboardSystems.Utils.Data;

import org.testng.annotations.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DataProviderHelper {

    private static final Logger logger = LoggerFactory.getLogger(DataProviderHelper.class);

    @DataProvider(name = "ExcelDataProvider")
    public static Object[][] getExcelData() throws FileNotFoundException {
        String filePath = "src/test/resources/testData.xlsx";
        ExcelReader excelReader = new ExcelReader(new FileInputStream(filePath));
        List<Map<String, String>> data = excelReader.getExcelData("Sheet1");

        return data.stream().map(row -> new Object[]{row}).toArray(Object[][]::new);
    }
}
