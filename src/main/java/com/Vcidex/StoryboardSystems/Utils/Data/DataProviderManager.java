package com.Vcidex.StoryboardSystems.Utils.Data;

import org.testng.annotations.DataProvider;
import java.util.Map;

public class DataProviderManager {

    @DataProvider(name = "SingleScenarioProvider")
    public static Object[][] getSingleScenarioData() {
        return new Object[][]{
                {"Scenario 1", ExcelReader.getScenarioData("Scenario 1")}  // Fixed lookup
        };
    }
}