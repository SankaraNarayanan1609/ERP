package com.Vcidex.StoryboardSystems.Common.Workflow;

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
        try {
            return productTypeCache.computeIfAbsent(poId, k -> {
                String jsonResponse = ExternalAPIService.fetchProductTypeFromAPI(poId);
                TestLogger.captureApiResponse(jsonResponse, poId);
                return productTypeCache.getOrDefault(poId, "INWARD");
            });
        } catch (Exception e) {
            logger.error("❌ Error fetching product type for PO [{}]: {}", poId, e.getMessage());
            return "INWARD"; // Default fallback
        }
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