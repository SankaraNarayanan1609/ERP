package com.Vcidex.StoryboardSystems.Utils.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static final String PROPERTIES_CONFIG_PATH = "config.properties";
    private static JSONObject jsonConfig;
    private static Properties properties = new Properties();

    // ✅ Load Configuration (JSON & Properties)
    static {
        try {
            loadJsonConfig();
            loadPropertiesConfig();
        } catch (ConfigException e) {
            logger.fatal("❌ ConfigManager initialization failed: {}", e.getMessage());
            throw e; // Stop execution if config loading fails
        }
    }

    /** ✅ Loads JSON-based configuration (config.json) */
    private static void loadJsonConfig() {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(jsonText);
            logger.info("✅ Loaded JSON configuration successfully.");
        } catch (IOException e) {
            logger.error("❌ Failed to load JSON config: {} | Exception: {}", JSON_CONFIG_PATH, e.getMessage());
            throw new ConfigException("Error loading JSON config file");
        }
    }

    /** ✅ Loads Properties-based configuration (config.properties) */
    private static void loadPropertiesConfig() {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(PROPERTIES_CONFIG_PATH)) {
            if (input != null) {
                properties.load(input);
                logger.info("✅ Loaded properties configuration successfully.");
            } else {
                logger.warn("⚠️ Properties file not found: {}", PROPERTIES_CONFIG_PATH);
            }
        } catch (IOException e) {
            logger.error("❌ Failed to load properties file | Exception: {}", e.getMessage());
            throw new ConfigException("Error loading properties file");
        }
    }

    /**
     * ✅ Retrieves App URL based on environment.
     * @param environment (test, staging)
     * @return App URL or empty string if not found
     */
    public static String getAppUrl(String environment) {
        JSONObject envConfig = jsonConfig.optJSONObject(environment);
        if (envConfig == null) {
            logger.warn("⚠️ No config found for environment: {}", environment);
            return "";
        }
        return envConfig.optString("appUrl", "");
    }

    /**
     * ✅ Retrieves user-specific data based on environment and index.
     * @param environment Environment name (test, staging)
     * @param userIndex   User index in the JSON array
     * @param dataType    The data field to fetch (companyCode, userName, password)
     * @return Data as String, or null if not found
     */
    public static String getUserData(String environment, int userIndex, String dataType) {
        JSONObject envConfig = jsonConfig.optJSONObject(environment);
        if (envConfig == null) {
            logger.warn("⚠️ Environment '{}' not found in JSON config.", environment);
            return null;
        }

        JSONArray users = envConfig.optJSONArray("users");
        if (users == null || users.length() <= userIndex) {
            logger.warn("⚠️ No user found at index {} for environment '{}'", userIndex, environment);
            return null;
        }

        JSONObject user = users.getJSONObject(userIndex);
        String data = user.optString(dataType, "");

        if ("password".equalsIgnoreCase(dataType)) {
            return decryptPassword(data);
        }
        return data.isEmpty() ? null : data;
    }

    /**
     * ✅ Retrieves a property value from config.properties
     * @param key Property key
     * @param defaultValue Default value if key is missing
     * @return Property value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /** ✅ Encrypts a password using Base64 encoding */
    public static String encryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    /** ✅ Decrypts a Base64 encoded password safely */
    private static String decryptPassword(String encryptedPassword) {
        try {
            return new String(Base64.getDecoder().decode(encryptedPassword));
        } catch (IllegalArgumentException e) {
            logger.error("❌ Failed to decode password: Invalid Base64 format");
            return "";
        }
    }

    /** ✅ Custom Exception for Config Handling */
    public static class ConfigException extends RuntimeException {
        public ConfigException(String message) {
            super(message);
        }
    }
}