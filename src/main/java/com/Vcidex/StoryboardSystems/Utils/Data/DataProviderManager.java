package com.Vcidex.StoryboardSystems.Utils.Data;

import org.testng.annotations.DataProvider;
import java.util.Map;

public class DataProviderManager {

    @DataProvider(name = "SingleScenarioProvider")
    public Object[][] getSingleScenarioData() {  // Removed 'static'
        return new Object[][]{
                {"Scenario 1", ExcelReader.getScenarioData("Scenario 1")}
        };
    }
}