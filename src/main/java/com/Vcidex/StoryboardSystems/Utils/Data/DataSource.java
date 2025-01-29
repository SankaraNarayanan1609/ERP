package com.Vcidex.StoryboardSystems.Utils.Data;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {
    String filePath();
    String pageName() default "DefaultPage";
    String scenarioID();
    String loginData() default "login.xlsx";
    String navigationData() default "navigation.xlsx";
}
