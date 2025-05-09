package com.Vcidex.StoryboardSystems.Utils.Data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 🔹 Custom annotation for specifying test data sources.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String filePath() default "src/test/resources/PurchaseTestData.xlsx";
    String pageName() default "DefaultPage";
    String scenarioID() default "Scenario1";
    String loginData() default "login.xlsx";
    String navigationData() default "navigation.xlsx";
}