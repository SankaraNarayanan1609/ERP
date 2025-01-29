package com.Vcidex.StoryboardSystems.Utils.Config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static final String PROPERTIES_CONFIG_PATH = "config.properties";
    private static JSONObject jsonConfig;
    private static Properties properties = new Properties();

    // Load Configuration (Both JSON & Properties)
    static {
        loadJsonConfig();
        loadPropertiesConfig();
    }

    /** Loads JSON-based configuration (config.json) */
    private static void loadJsonConfig() {
        try {
            String jsonText = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(jsonText);
            logger.info("Loaded JSON configuration successfully.");
        } catch (Exception e) {
            logger.error("Failed to load JSON configuration: {}", JSON_CONFIG_PATH, e);
            throw new RuntimeException("Error loading JSON config", e);
        }
    }

    /** Loads Properties-based configuration (config.properties) */
    private static void loadPropertiesConfig() {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(PROPERTIES_CONFIG_PATH)) {
            if (input != null) {
                properties.load(input);
                logger.info("Loaded properties configuration successfully.");
            } else {
                logger.error("Properties file not found: {}", PROPERTIES_CONFIG_PATH);
            }
        } catch (IOException e) {
            logger.error("Failed to load properties configuration", e);
            throw new RuntimeException("Error loading properties file", e);
        }
    }

    /**
     * Retrieves App URL based on the environment.
     *
     * @param environment (test, staging)
     * @return App URL
     */
    public static String getAppUrl(String environment) {
        return jsonConfig.optJSONObject(environment).optString("appUrl", "");
    }

    /**
     * Retrieves user-specific data based on environment and index.
     *
     * @param environment Environment name (test, staging)
     * @param userIndex   User index in the JSON array
     * @param dataType    The data field to fetch (companyCode, userName, password)
     * @return Data as String
     */
    public static String getUserData(String environment, int userIndex, String dataType) {
        JSONObject envConfig = jsonConfig.optJSONObject(environment);
        if (envConfig == null) throw new ConfigException("Environment not found: " + environment);

        JSONArray users = envConfig.optJSONArray("users");
        if (users == null || users.length() <= userIndex)
            throw new ConfigException("User not found at index: " + userIndex);

        JSONObject user = users.getJSONObject(userIndex);
        String data = user.optString(dataType, "");

        if (dataType.equals("password")) {
            return decryptPassword(data);
        }

        return data.isEmpty() ? null : data;
    }

    /**
     * Retrieves a property value from config.properties
     *
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key, "");
    }

    /**
     * Encrypts a password using Base64 encoding.
     *
     * @param password Plain text password
     * @return Base64 encoded password
     */
    public static String encryptPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes());
    }

    /**
     * Decrypts a Base64 encoded password.
     *
     * @param encryptedPassword Encrypted password string
     * @return Decrypted password
     */
    private static String decryptPassword(String encryptedPassword) {
        return new String(Base64.getDecoder().decode(encryptedPassword));
    }

    public static void load(String s) {
    }

    /** Custom Exception for Config Handling */
    public static class ConfigException extends RuntimeException {
        public ConfigException(String message) {
            super(message);
        }
    }
}