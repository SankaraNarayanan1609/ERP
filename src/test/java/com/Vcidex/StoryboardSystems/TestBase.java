// File: src/test/java/com/Vcidex/StoryboardSystems/TestBase.java
package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Purchase.Factory.ApiMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.DbMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.DelegatingMasterDataProvider;
import com.Vcidex.StoryboardSystems.Purchase.Factory.PurchaseOrderDataFactory;
import com.Vcidex.StoryboardSystems.Purchase.Factory.MasterDataProvider;
import com.Vcidex.StoryboardSystems.Utils.*;
import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

import javax.sql.DataSource;

public abstract class TestBase {
    protected static PurchaseOrderDataFactory factory;
    protected WebDriver driver;

    private static final String JDBC_URL    = "jdbc:mysql://localhost:3306/yourdb";
    private static final String DB_USERNAME = "your_user";
    private static final String DB_PASSWORD = "your_pass";
    private static final String API_BASE    = "https://your-api-host";
    private static final String API_TOKEN   = "YOUR_API_TOKEN";

    @BeforeClass
    public void setupSuite() {
        // 1) WebDriver setup
        String browser = System.getProperty("browser", "chrome");
        switch (browser.toLowerCase()) {
            case "chrome":
                driver = new ChromeDriver();
                break;
            case "firefox":
                driver = new FirefoxDriver();
                break;
            default:
                throw new IllegalArgumentException("Unsupported browser: " + browser);
        }
        driver.manage().window().maximize();
        ThreadSafeDriverManager.setDriver(driver);

        // 2) DataSource → MasterDataProviders → Factory
        DataSource ds = MysqlDataSourceFactory.create(JDBC_URL, DB_USERNAME, DB_PASSWORD);

        ApiMasterDataProvider apiProvider = new ApiMasterDataProvider(API_BASE, API_TOKEN);
        DbMasterDataProvider  dbProvider  = new DbMasterDataProvider(ds);

        DelegatingMasterDataProvider masterProvider =
                new DelegatingMasterDataProvider(apiProvider, dbProvider);

        factory = new PurchaseOrderDataFactory(masterProvider);
    }

    @AfterClass(alwaysRun = true)
    public void teardownSuite() {
        ThreadSafeDriverManager.removeDriver();
        if (driver != null) {
            driver.quit();
        }
    }
}