package com.Vcidex.StoryboardSystems.Common.Workflow;

import com.Vcidex.StoryboardSystems.Utils.Reporting.TestLogger;
import com.Vcidex.StoryboardSystems.Utils.API.ExternalAPIService;//Cannot resolve symbol 'API'
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

    private static void loadProductRules() {
        try {
            File file = new File("src/main/resources/product-rules.json");
            String content = new String(Files.readAllBytes(file.toPath()));
            JSONObject json = new JSONObject(content);

            json.keys().forEachRemaining(key -> productTypeRules.put(key.toUpperCase(), json.getString(key)));
            logger.info("✅ Product Rules Loaded: " + productTypeRules);
        } catch (Exception e) {
            logger.error("❌ Error loading product rules: " + e.getMessage());
        }
    }

    public static void updateProductTypeRules(String poId, String productType) {
        if (productType == null || productType.isEmpty()) {
            logger.warn("⚠️ Product Type Unknown for PO: " + poId);
            return;
        }
        productTypeCache.put(poId, productType.toUpperCase());
        logger.info("🔄 Updated Product Type for PO [" + poId + "]: " + productType);
    }

    public static String getProductType(String poId) {
        return productTypeCache.computeIfAbsent(poId, k -> {
            logger.warn("⚠️ Product Type Missing for PO: " + poId + ". Fetching from API...");
            String jsonResponse = ExternalAPIService.fetchProductTypeFromAPI(poId); // ✅ Fixed: Implemented ExternalAPIService
            TestLogger.captureApiResponse(jsonResponse, poId);
            return productTypeCache.getOrDefault(poId, "INWARD");
        });
    }

    public static String getWorkflowStageForProductType(String productType) {
        return productTypeRules.getOrDefault(productType.toUpperCase(), "INWARD");
    }
}