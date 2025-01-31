package com.Vcidex.StoryboardSystems.Purchase.Test.Flow;

import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import org.testng.annotations.Test;

public class DirectPOFlow extends TestBase {

    public void testPurchaseOrder() {
        try {
            logger.info("🚀 Starting Purchase Order Test...");

            // ✅ Initialize Page Object
            Direct_PO directPO = new Direct_PO(driver);
        } catch (Exception e) {
            logger.error("❌ Test Failed: ", e);
            captureScreenshot("DirectPO_Failure_" + System.currentTimeMillis());
            Assert.fail("Test Failed due to Exception: " + e.getMessage());
        }
    }
}
