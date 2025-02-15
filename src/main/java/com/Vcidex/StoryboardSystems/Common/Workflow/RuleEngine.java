package com.Vcidex.StoryboardSystems.Common.Workflow;

import com.Vcidex.StoryboardSystems.Utils.Database.DatabaseService;
import com.Vcidex.StoryboardSystems.Utils.Reporting.TestLogger;
import com.Vcidex.StoryboardSystems.Utils.API.ExternalAPIService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class RuleEngine {
    private static final Logger logger = LogManager.getLogger(RuleEngine.class);
    private static final Map<String, String> productTypeCache = new ConcurrentHashMap<>();
    private static final Map<String, String> productTypeRules = new ConcurrentHashMap<>();

    static {
        loadProductRules();
    }

    /**
     * ✅ Loads product type rules from `product-rules.json`
     */
    private static void loadProductRules() {
        try {
            File file = new File("src/main/resources/product-rules.json");
            String content = new String(Files.readAllBytes(file.toPath()));
            JSONObject json = new JSONObject(content);

            json.keys().forEachRemaining(key -> productTypeRules.put(key.toUpperCase(), json.getString(key)));
            logger.info("✅ Product Rules Loaded: {}", productTypeRules);
        } catch (Exception e) {
            logger.error("❌ Error loading product rules: {}", e.getMessage());
        }
    }

    /**
     * ✅ Evaluates a predefined rule based on rule key.
     */
    public static boolean evaluateRule(String ruleKey) {
        try {
            String ruleValue = productTypeRules.getOrDefault(ruleKey.toUpperCase(), "false");
            boolean result = Boolean.parseBoolean(ruleValue);
            logger.info("🔍 Rule Evaluated: {} → {}", ruleKey, result);
            return result;
        } catch (Exception e) {
            logger.error("❌ Error evaluating rule '{}': {}", ruleKey, e.getMessage());
            return false;
        }
    }

    /**
     * ✅ Updates product type mapping for a given PO ID.
     */
    public static void updateProductTypeRules(String poId, String productType) {
        try {
            if (productType == null || productType.isEmpty()) {
                logger.warn("⚠️ Product Type Unknown for PO: {}", poId);
                return;
            }
            productTypeCache.put(poId, productType.toUpperCase());
            logger.info("🔄 Updated Product Type for PO [{}]: {}", poId, productType);
        } catch (Exception e) {
            logger.error("❌ Error updating product type rules for PO [{}]: {}", poId, e.getMessage());
        }
    }

    /**
     * ✅ Fetches product type from cache or API.
     */
    public static String getProductType(String poId) {
        return productTypeCache.computeIfAbsent(poId, k -> {
            int retries = 3;
            while (retries > 0) {
                try {
                    String jsonResponse = ExternalAPIService.fetchProductTypeFromAPI(poId);
                    JSONObject response = new JSONObject(jsonResponse);
                    String productType = response.optString("productType", "INWARD");
                    productTypeCache.put(poId, productType);
                    return productType;
                } catch (Exception e) {
                    retries--;
                    logger.warn("⚠️ Failed to fetch product type for PO [{}], Retrying... {} attempts left", poId, retries);
                }
            }
            return "INWARD";
        });
    }

    /**
     * ✅ Retrieves workflow stage based on product type.
     */
    public static String getWorkflowStageForProductType(String productType) {
        try {
            if (productType == null || productType.isEmpty()) {
                logger.warn("⚠️ Empty product type received, defaulting to 'INWARD'");
                return "INWARD";
            }

            String workflowStage = productTypeRules.getOrDefault(productType.toUpperCase(), "INWARD");
            logger.info("🔄 Retrieved Workflow Stage: Product Type [{}] -> Workflow Stage [{}]", productType, workflowStage);
            return workflowStage;
        } catch (Exception e) {
            logger.error("❌ Error retrieving workflow stage for product type [{}]: {}", productType, e.getMessage());
            return "INWARD"; // Default fallback
        }
    }
}