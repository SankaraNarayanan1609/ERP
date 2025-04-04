package com.Vcidex.StoryboardSystems.Utils.Config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ConfigManager {
    private static final Logger logger = LogManager.getLogger(ConfigManager.class);
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static JSONObject jsonConfig;

    // Static block to initialize configuration
    static {
        initializeConfig();
    }

    /**
     * ✅ Initialize JSON configuration
     */
    private static void initializeConfig() {
        try {
            if (!Files.exists(Paths.get(JSON_CONFIG_PATH))) {
                logger.error("❌ Config file '{}' not found!", JSON_CONFIG_PATH);
                throw new RuntimeException("Config file not found: " + JSON_CONFIG_PATH);
            }

            String jsonText = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
                    jsonConfig = new JSONObject(jsonText);

            logger.info("✅ JSON configuration successfully loaded: \n{}", jsonConfig.toString(2));
        } catch (IOException e) {
            logger.error("❌ Failed to load JSON configuration: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize JSON configurations.", e);
        }
    }

    /**
     * ✅ Generic method to retrieve JSON config values as String
     */
    public static String getConfig(String environment, String key) {
        try {
            if (!jsonConfig.has(environment)) {
                logger.error("❌ Environment '{}' not found in config!", environment);
                return "";
            }

            JSONObject envConfig = jsonConfig.getJSONObject(environment);
            if (!envConfig.has(key)) {
                logger.error("❌ Key '{}' not found in environment '{}'.", key, environment);
                return "";
            }

            String value = envConfig.optString(key, "");
            logger.info("🔍 Retrieved config '{}' = '{}' from environment '{}'", key, value, environment);
            return value;
        } catch (Exception e) {
            logger.error("❌ Error retrieving config '{}' for env '{}': {}", key, environment, e.getMessage());
            return "";
        }
    }

    /**
     * ✅ Retrieves a List of Maps from a JSON array in the config
     */
    public static List<Map<String, String>> getConfigList(String environment, String key) {
        List<Map<String, String>> dataList = new ArrayList<>();
        try {
            if (!jsonConfig.has(environment) || !jsonConfig.getJSONObject(environment).has(key)) {
                logger.warn("⚠️ Config key '{}' not found for environment '{}'.", key, environment);
                return Collections.emptyList();
            }
            JSONArray jsonArray = jsonConfig.getJSONObject(environment).getJSONArray(key);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                Map<String, String> dataMap = new HashMap<>();

                for (String objKey : obj.keySet()) {
                    dataMap.put(objKey, obj.optString(objKey, ""));
                }
                dataList.add(dataMap);
            }
        } catch (Exception e) {
            logger.error("❌ Error retrieving JSON array '{}' for env '{}': {}", key, environment, e.getMessage());
        }
        return dataList;
    }

    public static Map<String, String> getUserById(String environment, String userId) {
        List<Map<String, String>> users = getConfigList(environment, "users");
        for (Map<String, String> user : users) {
            if (user.get("userId").equals(userId)) {
                return user;
            }
        }
        return null; // Return null if no matching user is found
    }

    public static String getProperty(String key, String defaultValue) {
        Properties properties = new Properties();
        String propertiesPath = "config.properties";

        if (!Files.exists(Paths.get(propertiesPath))) {
            logger.warn("⚠️ Properties file '{}' not found. Using default value for '{}'.", propertiesPath, key);
            return defaultValue;
        }

        try (InputStream input = new FileInputStream(propertiesPath)) {
            properties.load(input);
            String value = properties.getProperty(key, defaultValue);
            logger.info("🔍 Retrieved property '{}' = '{}'", key, value);
            return value;
        } catch (IOException e) {
            logger.error("❌ Error reading properties file '{}': {}", propertiesPath, e.getMessage());
            return defaultValue;
        }
    }

    /**
     * ✅ Retrieves all available environment names in the config
     */
    public static Set<String> getAvailableEnvironments() {
        return jsonConfig.keySet();
    }
}