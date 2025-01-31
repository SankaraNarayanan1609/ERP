package com.Vcidex.StoryboardSystems.Purchase.Test.Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.openqa.selenium.WebDriver;

public class DirectPOFlow extends TestBase {

    @Test
    public void testPurchaseOrder() {
        try {
            System.out.println("🚀 Starting Purchase Order Test...");

            WebDriver driver = getDriver(); // ✅ Get the driver from TestBase

            // ✅ Ensure WebDriver is initialized
            if (driver == null) {
                Assert.fail("❌ WebDriver is not initialized. Check WebDriverFactory setup.");
            }

            // ✅ Initialize Page Object
            Direct_PO directPO = new Direct_PO(driver);

            // ✅ (Optional) Add UI actions here (e.g., directPO.createOrder())

        } catch (Exception e) {
            System.err.println("❌ Test Failed: " + e.getMessage());
            Assert.fail("Test Failed due to Exception: " + e.getMessage());
        }
    }
}
