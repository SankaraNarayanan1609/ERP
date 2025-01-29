package com.Vcidex.StoryboardSystems.Common.Authentication;

import org.json.JSONArray;
import org.json.JSONObject;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AuthConfig {
    private static JSONObject config;

    public static void loadConfig(String configPath) {
        try {
            String jsonText = Files.readString(Paths.get(configPath));
            config = new JSONObject(jsonText);
        } catch (Exception e) {
            throw new ConfigFileException("Failed to load config file: " + configPath, e);
        }
    }

    public static String getAppUrl(String environment) {
        if (config == null) {
            loadConfig("src/main/resources/config.json");
        }
        return config.optJSONObject(environment).optString("appUrl", "");
    }

    public static String getUserData(String environment, int userIndex, String dataType) {
        if (config == null) {
            loadConfig("src/main/resources/config.json");
        }

        JSONObject envConfig = config.optJSONObject(environment);
        if (envConfig == null) {
            throw new ConfigFileException("Environment not found: " + environment);
        }

        JSONArray users = envConfig.optJSONArray("users");
        if (users == null || users.length() <= userIndex) {
            throw new ConfigFileException("User not found at index: " + userIndex);
        }

        JSONObject user = users.getJSONObject(userIndex);
        String data = user.optString(dataType, "");
        if (data.isEmpty()) {
            throw new ConfigFileException(dataType + " not found for user index: " + userIndex);
        }

        return data;
    }

    // Custom exception for configuration-related errors
    public static class ConfigFileException extends RuntimeException {
        public ConfigFileException(String message) {
            super(message);
        }

        public ConfigFileException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
