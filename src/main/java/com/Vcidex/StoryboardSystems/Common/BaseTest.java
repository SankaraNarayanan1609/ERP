package com.Vcidex.StoryboardSystems.Common;

import com.Vcidex.StoryboardSystems.Utils.Reporting.ExtentManager;
import org.testng.annotations.AfterSuite;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BaseTest {
    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    @AfterSuite
    public void tearDown() {
        System.out.println("🔄 [DEBUG] Triggering Extent Report flush...");
        ExtentManager.flushReports();
        logger.info("✅ [DEBUG] Extent Reports Flushed Successfully.");
    }
}