package com.Vcidex.StoryboardSystems;

import com.Vcidex.StoryboardSystems.Utils.ThreadSafeDriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected WebDriver driver;

    public void setUpDriver() {
        // your driver initialization
        this.driver = new ChromeDriver(); // or EdgeDriver etc.
        ThreadSafeDriverManager.setDriver(this.driver); // 🔥 This is important
    }



    @AfterClass
    public void tearDownDriver() {
        if (driver != null) {
            driver.quit();                        // Ensures browser is closed properly
        }
    }
}