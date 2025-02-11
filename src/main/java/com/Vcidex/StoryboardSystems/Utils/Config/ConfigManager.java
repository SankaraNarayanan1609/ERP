package com.Vcidex.StoryboardSystems.Utils.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static final String PROPERTIES_CONFIG_PATH = "src/main/resources/config.properties";
    private static JSONObject jsonConfig;
    private static Properties properties = new Properties();

    static {
        loadJsonConfig();
        loadPropertiesConfig();
    }

    /**
     * ✅ Load JSON-based configuration (config.json)
     */
    private static void loadJsonConfig() {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(jsonText);
            logger.info("✅ Loaded JSON configuration successfully.");
        } catch (IOException e) {
            logger.error("❌ Failed to load JSON config: {}", e.getMessage());
            throw new RuntimeException("Error loading JSON config file");
        }
    }

    /**
     * ✅ Load properties-based configuration (config.properties)
     */
    private static void loadPropertiesConfig() {
        try {
            properties.load(Files.newBufferedReader(Paths.get(PROPERTIES_CONFIG_PATH)));
            logger.info("✅ Loaded properties configuration successfully.");
        } catch (IOException e) {
            logger.error("❌ Failed to load properties file: {}", e.getMessage());
            throw new RuntimeException("Error loading properties file");
        }
    }

    /**
     * ✅ Retrieve application URL based on environment
     */
    public static String getAppUrl(String environment) {
        try {
            return jsonConfig.optJSONObject(environment).optString("appUrl", "");
        } catch (Exception e) {
            logger.error("❌ Error fetching App URL for environment '{}': {}", environment, e.getMessage());
            return "";
        }
    }

    /**
     * ✅ Retrieve user-specific data from JSON config
     */
    public static String getUserData(String environment, int userIndex, String dataType) {
        try {
            JSONObject envConfig = jsonConfig.optJSONObject(environment);
            if (envConfig == null) {
                logger.warn("⚠️ No config found for environment: {}", environment);
                return null;
            }
            return envConfig.optJSONArray("users").optJSONObject(userIndex).optString(dataType, "");
        } catch (Exception e) {
            logger.error("❌ Error fetching user data ({}): {}", dataType, e.getMessage());
            return null;
        }
    }

    /**
     * ✅ Retrieve a property value from config.properties
     */
    public static String getProperty(String key, String defaultValue) {
        try {
            return properties.getProperty(key, defaultValue);
        } catch (Exception e) {
            logger.error("❌ Error fetching property '{}': {}", key, e.getMessage());
            return defaultValue;
        }
    }
}