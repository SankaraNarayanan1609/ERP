package com.Vcidex.StoryboardSystems.Utils.Config;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

/**
 * 🔧 ConfigManager handles hierarchical config from:
 * - config.json → for env → app → company → user
 * - config.properties → for runtime overrides like env/user/app + DB, timeouts, etc.
 */
public class ConfigManager {

    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static final String PROP_FILE = "config.properties";

    private static JSONObject jsonConfig;
    private static Properties prop = new Properties();

    static {
        try {
            // Load JSON config
            String text = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(text);
            logger.info("✅ Loaded JSON config: {}", JSON_CONFIG_PATH);

            // Load properties
            try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(PROP_FILE)) {
                if (in != null) {
                    prop.load(in);
                    logger.info("✅ Loaded properties: {}", PROP_FILE);
                } else {
                    logger.warn("⚠️ Properties file not found: {}", PROP_FILE);
                }
            }

        } catch (Exception e) {
            logger.error("❌ ConfigManager init failed: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Basic Getters (From .properties)
    // ──────────────────────────────────────────────────────────────────────────────

    public static String getEnv() {
        return prop.getProperty("env", "test");
    }

    public static String getAppName() {
        String userId = getRawUserId(); // From properties (or fallback)
        return getUserAppName(getEnv(), userId); // New method
    }

    public static String getRawUserId() {
        return prop.getProperty("userId", "qa.tester");
    }

    public static String getProperty(String key, String defaultValue) {
        return prop.getProperty(key, defaultValue);
    }

    public static String getUserAppName(String env, String userId) {
        JSONObject envObj = jsonConfig.optJSONObject(env);
        if (envObj == null) throw new IllegalArgumentException("Invalid env: " + env);

        JSONArray apps = envObj.optJSONArray("applications");
        if (apps == null) throw new IllegalArgumentException("No apps for env: " + env);

        for (Object o : apps) {
            JSONObject app = (JSONObject) o;
            JSONArray companies = app.optJSONArray("companies");
            if (companies == null) continue;

            for (Object c : companies) {
                JSONObject company = (JSONObject) c;
                JSONArray users = company.optJSONArray("users");
                if (users == null) continue;

                for (Object u : users) {
                    JSONObject user = (JSONObject) u;
                    if (userId.equals(user.optString("userId"))) {
                        return app.optString("appName");
                    }
                }
            }
        }

        throw new IllegalArgumentException("App not found for user: " + userId);
    }

    public static Set<String> getEnvironments() {
        return jsonConfig.keySet();
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // JSON-Based Config Resolution
    // ──────────────────────────────────────────────────────────────────────────────

    public static JSONObject getAppConfig(String env, String appName) {
        JSONObject envObj = jsonConfig.optJSONObject(env);
        if (envObj == null) throw new IllegalArgumentException("Invalid env: " + env);

        JSONArray apps = envObj.optJSONArray("applications");
        if (apps == null) throw new IllegalArgumentException("No apps in env: " + env);

        for (Object o : apps) {
            JSONObject app = (JSONObject) o;
            if (appName.equals(app.optString("appName"))) return app;
        }

        throw new IllegalArgumentException("App not found: " + appName + " in env: " + env);
    }

    public static JSONObject getCompanyConfig(String env, String appName, String userId) {
        JSONObject app = getAppConfig(env, appName);
        JSONArray companies = app.optJSONArray("companies");

        if (companies == null) throw new IllegalArgumentException("No companies for app: " + appName);

        for (Object o : companies) {
            JSONObject company = (JSONObject) o;
            JSONArray users = company.optJSONArray("users");

            if (users != null) {
                for (Object u : users) {
                    JSONObject user = (JSONObject) u;
                    if (userId.equals(user.optString("userId"))) {
                        return company;
                    }
                }
            }
        }

        throw new IllegalArgumentException("No company found for user: " + userId);
    }

    public static JSONObject getUserConfig(String env, String appName, String userId) {
        JSONObject company = getCompanyConfig(env, appName, userId);
        JSONArray users = company.optJSONArray("users");

        if (users == null) throw new IllegalArgumentException("No users in company: " + company.optString("companyCode"));

        for (Object u : users) {
            JSONObject user = (JSONObject) u;
            if (userId.equals(user.optString("userId"))) return user;
        }

        throw new IllegalArgumentException("User not found: " + userId);
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // Final Utility Getters
    // ──────────────────────────────────────────────────────────────────────────────

    public static String getUserId() {
        return getUserConfig(getEnv(), getAppName(), getRawUserId()).getString("userId");
    }

    public static String getCompanyCode() {
        String userId = getRawUserId(); // From properties (or fallback)
        return getCompanyConfig(getEnv(), getAppName(), userId).getString("companyCode");
    }

    // 🔄 Overloaded method for LoginManager (userId only)
    public static JSONObject getUserConfig(String userId) {
        String env = getEnv();
        String appName = getAppName();
        String companyCode = getCompanyCode(); // Already resolved based on userId

        return getUserConfig(env, appName, companyCode, userId); // Cannot resolve method 'getUserConfig(String, String, String, String)'
    }

    // ✅ 4-arg overload to support dynamic user resolution
    public static JSONObject getUserConfig(String env, String appName, String companyCode, String userId) {
        JSONObject company = getCompanyConfig(env, appName, userId);  // already matches company by userId
        JSONArray users = company.optJSONArray("users");

        if (users == null)
            throw new IllegalArgumentException("No users found in company: " + companyCode);

        for (Object u : users) {
            JSONObject user = (JSONObject) u;
            if (userId.equals(user.optString("userId"))) {
                return user;
            }
        }

        throw new IllegalArgumentException("User not found: " + userId + " in company: " + companyCode);
    }

    public static String getApiBase() {
        return getAppConfig(getEnv(), getAppName()).optString("apiBase");
    }

    public static String getAppUrl() {
        return getAppConfig(getEnv(), getAppName()).optString("appUrl");
    }

    public static String getLoginUrl() {
        return getAppConfig(getEnv(), getAppName()).optString("loginUrl");
    }
}