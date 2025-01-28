package com.Vcidex.StoryboardSystems.Utils.Data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String filePath();       // Path to the Excel file (e.g., "PurchaseData.xlsx")
    String pageName() default "";  // Page name for page-specific data (default is empty)
    String scenarioID();      // Scenario ID for either page or flow data
    String loginData() default ""; // Optional: Path to login data
    String navigationData() default ""; // Optional: Path to navigation data
}