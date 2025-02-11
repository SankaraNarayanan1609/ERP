package com.Vcidex.StoryboardSystems.Utils.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static JSONObject jsonConfig;

    static {
        loadJsonConfig();
    }

    private static void loadJsonConfig() {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(jsonText);
            logger.info("✅ Loaded JSON configuration successfully.");
        } catch (Exception e) {
            logger.error("❌ Failed to load JSON config: {}", e.getMessage());
            throw new RuntimeException("Error loading JSON config file");
        }
    }

    public static String getAppUrl(String environment) {
        return jsonConfig.optJSONObject(environment).optString("appUrl", "");
    }

    public static String getUserData(String environment, int userIndex, String dataType) {
        JSONObject envConfig = jsonConfig.optJSONObject(environment);
        if (envConfig == null) {
            logger.warn("⚠️ No config found for environment: {}", environment);
            return null;
        }
        return envConfig.optJSONArray("users").optJSONObject(userIndex).optString(dataType, "");
    }
}