package com.Vcidex.StoryboardSystems.Utils.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.Set;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";
    private static final String PROP_FILE = "config.properties";
    private static JSONObject jsonConfig;

    static {
        try {
            String text = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));
            jsonConfig = new JSONObject(text);
            logger.info("✅ Loaded JSON config: {}", JSON_CONFIG_PATH);
        } catch (Exception e) {
            logger.error("❌ Failed to load JSON config {}: {}", JSON_CONFIG_PATH, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // ────────────────────────────────────────────────────────────────────────────

    public static JSONObject getAppConfig(String env, String appName) {
        JSONObject envObj = jsonConfig.optJSONObject(env);
        if (envObj == null) {
            throw new IllegalArgumentException("Environment not found: " + env);
        }
        JSONArray apps = envObj.optJSONArray("applications");
        if (apps == null) {
            throw new IllegalArgumentException("No applications defined for env: " + env);
        }
        for (Object o : apps) {
            JSONObject app = (JSONObject) o;
            if (appName.equals(app.optString("appName"))) {
                return app;
            }
        }
        throw new IllegalArgumentException("App not found: " + appName + " in env: " + env);
    }

    public static JSONObject getCompanyConfig(String env, String appName, String companyCode) {
        JSONObject app = getAppConfig(env, appName);
        JSONArray companies = app.optJSONArray("companies");
        if (companies == null) {
            throw new IllegalArgumentException("No companies for app: " + appName);
        }
        for (Object o : companies) {
            JSONObject comp = (JSONObject) o;
            if (companyCode.equals(comp.optString("companyCode"))) {
                return comp;
            }
        }
        throw new IllegalArgumentException("Company not found: " + companyCode + " in app: " + appName);
    }

    public static JSONObject getUserConfig(String env, String appName, String companyCode, String userId) {
        JSONObject comp = getCompanyConfig(env, appName, companyCode);
        JSONArray users = comp.optJSONArray("users");
        if (users == null) {
            throw new IllegalArgumentException("No users for company: " + companyCode);
        }
        for (Object o : users) {
            JSONObject u = (JSONObject) o;
            if (userId.equals(u.optString("userId"))) {
                return u;
            }
        }
        throw new IllegalArgumentException("User not found: " + userId + " in company: " + companyCode);
    }

    /** New: load the "auth" block under each env */
    public static JSONObject getAuthConfig(String env) {
        JSONObject envObj = jsonConfig.optJSONObject(env);
        if (envObj == null) {
            throw new IllegalArgumentException("Environment not found: " + env);
        }
        JSONObject authObj = envObj.optJSONObject("auth");
        if (authObj == null) {
            throw new IllegalArgumentException("No auth block defined for env: " + env);
        }
        return authObj;
    }

    // ────────────────────────────────────────────────────────────────────────────

    public static String getProperty(String key, String defaultValue) {
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(PROP_FILE)) {
            if (in == null) {
                logger.warn("Properties file '{}' not found, using default for {}", PROP_FILE, key);
                return defaultValue;
            }
            Properties props = new Properties();
            props.load(in);
            String val = props.getProperty(key, defaultValue);
            logger.info("🔍 Loaded property '{}' = '{}'", key, val);
            return val;
        } catch (Exception e) {
            logger.error("❌ Failed to load properties: {}", e.getMessage(), e);
            return defaultValue;
        }
    }

    public static Set<String> getEnvironments() {
        return jsonConfig.keySet();
    }
}