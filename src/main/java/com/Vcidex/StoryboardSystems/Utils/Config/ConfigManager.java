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

    // Static block to initialize configuration
    static {
        initializeConfig();
    }

    /**
     * ✅ Initialize all configurations (JSON + Properties)
     */
    private static void initializeConfig() {
        try {
            // Load JSON Config
            String jsonText = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(jsonText);
            logger.info("✅ JSON configuration loaded from: {}", JSON_CONFIG_PATH);

            // Load Properties Config
            properties.load(Files.newBufferedReader(Paths.get(PROPERTIES_CONFIG_PATH)));
            logger.info("✅ Properties configuration loaded from: {}", PROPERTIES_CONFIG_PATH);

        } catch (IOException e) {
            logger.error("❌ Failed to load configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize configurations.", e);
        }
    }

    /**
     * ✅ Generic method to retrieve JSON config values
     */
    public static String getConfig(String environment, String key) {
        try {
            JSONObject envConfig = jsonConfig.getJSONObject(environment);
            return envConfig.getString(key);
        } catch (Exception e) {
            logger.error("❌ Error retrieving config '{}' for env '{}': {}", key, environment, e.getMessage());
            return "";
        }
    }

    /**
     * ✅ Retrieve application URL
     */
    public static String getAppUrl(String environment) {
        return getConfig(environment, "appUrl");
    }

    /**
     * ✅ Retrieve user-specific data
     */
    public static String getUserData(String environment, int userIndex, String dataType) {
        try {
            JSONObject envConfig = jsonConfig.getJSONObject(environment);
            return envConfig.getJSONArray("users").getJSONObject(userIndex).getString(dataType);
        } catch (Exception e) {
            logger.error("❌ Error fetching user data ('{}') for userIndex {} in env '{}': {}", dataType, userIndex, environment, e.getMessage());
            return "";
        }
    }

    /**
     * ✅ Retrieve a property value from config.properties
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}