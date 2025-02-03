package com.Vcidex.StoryboardSystems.Purchase.Test.Flow;

import com.Vcidex.StoryboardSystems.Common.Base.BasePage;
import com.Vcidex.StoryboardSystems.Common.Base.TestBase;
import com.Vcidex.StoryboardSystems.Purchase.Pages.Purchase_Order.Direct_PO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DirectPOFlow extends TestBase {
    private static final Logger logger = LogManager.getLogger(DirectPOFlow.class);

    @Test
    public void testPurchaseOrder() {
        try {
            logger.info("🚀 Starting Purchase Order Test...");
            Direct_PO directPO = new Direct_PO(driver);
        } catch (Exception e) {
            logger.error("❌ Test Failed: ", e);

            // ✅ Use BasePage instance to call captureScreenshot
            new BasePage(driver).captureScreenshot("DirectPO_Failure_" + System.currentTimeMillis());

            Assert.fail("Test Failed due to Exception: " + e.getMessage());
        }
    }
}