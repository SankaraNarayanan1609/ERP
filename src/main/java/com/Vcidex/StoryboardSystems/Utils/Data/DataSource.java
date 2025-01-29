package com.Vcidex.StoryboardSystems.Utils.Data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation for specifying test data source.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String filePath();
    String pageName() default "DefaultPage";
    String scenarioID();
    String loginData() default "login.xlsx";
    String navigationData() default "navigation.xlsx";
}
