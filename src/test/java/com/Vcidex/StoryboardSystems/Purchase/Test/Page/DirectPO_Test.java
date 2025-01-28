package com.Vcidex.StoryboardSystems.Purchase.Test.Page;

import com.Vcidex.StoryboardSystems.Common.Authentication.LoginManager;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

public class DirectPO_Test {
    WebDriver driver = new ChromeDriver(); // Or the WebDriver instance you're using

    @Test
    public void testCreateDirectPO() {
        // Navigate to Direct PO page
        Direct_PO directPO = new Direct_PO(driver);

        // Set up file path and terms (for example, from a test data source)
        String filePath = "path/to/your/file";
        String terms = "Standard Terms";

        // Call the wrapper method to create the Direct PO
        directPO.createDirectPO(filePath, terms);

        // Add assertions to verify successful Direct PO creation
        String confirmationMessage = directPO.getConfirmationMessage(); // Implement this method in Direct_PO
        assertTrue(confirmationMessage.contains("successfully created"), "Direct PO was not created successfully");
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }
}