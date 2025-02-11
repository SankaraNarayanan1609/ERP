package com.Vcidex.StoryboardSystems.Utils.Data;

import org.testng.annotations.DataProvider;
import java.util.List;
import java.util.Map;

public class DataProviderManager {

    @DataProvider(name = "SingleScenarioProvider")
    public static Object[][] getSingleScenarioData() {
        return new Object[][]{
                {"Scenario_1", ExcelReader.getScenarioData("Scenario_1")}
        };
    }

    @DataProvider(name = "MultipleScenarioProvider")
    public static Object[][] getMultipleScenariosData() {
        List<String> scenarios = List.of("Scenario_1", "Scenario_3"); // Change as needed
        return scenarios.stream()
                .map(scenario -> new Object[]{scenario, ExcelReader.getScenarioData(scenario)})
                .toArray(Object[][]::new);
    }

    @DataProvider(name = "AllScenariosProvider")
    public static Object[][] getAllScenariosData() {
        List<String> allScenarios = ExcelReader.getAllScenarios();
        return allScenarios.stream()
                .map(scenario -> new Object[]{scenario, ExcelReader.getScenarioData(scenario)})
                .toArray(Object[][]::new);
    }
}