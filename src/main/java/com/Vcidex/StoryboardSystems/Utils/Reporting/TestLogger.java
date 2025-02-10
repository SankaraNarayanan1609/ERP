package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.Vcidex.StoryboardSystems.Common.Workflow.RuleEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.openqa.selenium.chrome.ChromeDriver;

public class TestLogger {
    private static final Logger logger = LogManager.getLogger(TestLogger.class);

    public TestLogger(ChromeDriver driver) {
    }

    public static void captureApiResponse(String jsonResponse, String poId) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            String productType = json.optString("productType", "Unknown");

            if (!"Unknown".equals(productType)) {
                RuleEngine.updateProductTypeRules(poId, productType); // ✅ Centralized Storage in RuleEngine
                logger.info("✅ Cached Product Type: PO " + poId + " -> " + productType);
            }
        } catch (Exception e) {
            logger.error("❌ Error parsing API response: " + e.getMessage());
        }
    }
}