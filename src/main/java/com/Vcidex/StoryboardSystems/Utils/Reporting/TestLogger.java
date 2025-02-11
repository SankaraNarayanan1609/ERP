package com.Vcidex.StoryboardSystems.Utils.Reporting;

import com.Vcidex.StoryboardSystems.Common.Workflow.RuleEngine;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class TestLogger {
    private static final Logger logger = LogManager.getLogger(TestLogger.class);

    public static void captureApiResponse(String jsonResponse, String poId) {
        try {
            JSONObject json = new JSONObject(jsonResponse);
            String productType = json.optString("productType", "Unknown");

            if (!"Unknown".equals(productType)) {
                RuleEngine.updateProductTypeRules(poId, productType);
                logger.info("✅ Cached Product Type: PO {} -> {}", poId, productType);

                // ✅ Save API response to a log file
                try (FileWriter writer = new FileWriter("test-output/api-responses.log", true)) {
                    writer.write("PO: " + poId + " | Response: " + jsonResponse + "\n");
                }
            }
        } catch (Exception e) {
            logger.error("❌ Error parsing API response: {}", e.getMessage());
        }
    }
}