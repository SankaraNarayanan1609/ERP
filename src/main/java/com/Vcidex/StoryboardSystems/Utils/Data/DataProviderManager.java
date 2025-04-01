package com.Vcidex.StoryboardSystems.Utils.Data;

import org.testng.annotations.DataProvider;
import java.lang.reflect.Method;
import java.util.Map;

public class DataProviderManager {

    @DataProvider(name = "SingleScenarioProvider")
    public Object[][] getSingleScenarioData(Method method) {
        DataSource dataSource = method.getAnnotation(DataSource.class);
        String scenarioID = (dataSource != null) ? dataSource.scenarioID() : "Scenario1";
        String filePath = (dataSource != null) ? dataSource.filePath() : System.getProperty("user.dir") + "/src/test/resources/defaultTestData.xlsx";

        return new Object[][]{
                {scenarioID, ExcelReader.getScenarioData(scenarioID, filePath)}
        };
    }
}