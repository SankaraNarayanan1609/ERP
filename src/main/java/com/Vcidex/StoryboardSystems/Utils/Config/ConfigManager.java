/**
 * ConfigManager is responsible for loading environment-specific configurations
 * from both:
 *  - a JSON file (used for structured application, company, user, and auth data)
 *  - a properties file (used for basic key-value pairs)
 *
 * 📂 JSON config path: `src/main/resources/config.json`
 * 📂 Properties path:  `config.properties`
 *
 * 💡 Key Capabilities:
 * - Fetch config per environment, application, company, user, and auth section
 * - Load fallback values from properties if JSON is missing
 * - Centralized utility for config-driven test and framework behavior
 */

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

    // SLF4J logger for logging success/failure of config loading
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);

    // Path to the static JSON configuration file (usually version-controlled)
    private static final String JSON_CONFIG_PATH = "src/main/resources/config.json";

    // Properties file name in classpath (used for fallback or simple key-values)
    private static final String PROP_FILE = "config.properties";

    // Holds the full parsed config.json structure
    private static JSONObject jsonConfig;

    // ─── Static Initializer: Load config.json once on class load ──────────────────────
    static {
        try {
            // Read the file content into a string
            String text = new String(Files.readAllBytes(Paths.get(JSON_CONFIG_PATH)));

            // Parse it into a JSONObject
            jsonConfig = new JSONObject(text);

            logger.info("✅ Loaded JSON config: {}", JSON_CONFIG_PATH);
        } catch (Exception e) {
            // Log and rethrow as runtime to halt tests that depend on this
            logger.error("❌ Failed to load JSON config {}: {}", JSON_CONFIG_PATH, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    // ──────────────────────────────────────────────────────────────────────────────
    // SECTION: JSON Config Extraction
    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Fetches the config block for a specific application inside a specific environment.
     *
     * @param env     The environment key (e.g., "test", "staging", "prod")
     * @param appName The application name to lookup under that environment
     * @return JSONObject representing the selected application's config
     * @throws IllegalArgumentException if the environment or appName is not found
     */
    public static JSONObject getAppConfig(String env, String appName) {
        JSONObject envObj = jsonConfig.optJSONObject(env);  // Top-level environment
        if (envObj == null) {
            throw new IllegalArgumentException("Environment not found: " + env);
        }

        JSONArray apps = envObj.optJSONArray("applications");  // List of apps under env
        if (apps == null) {
            throw new IllegalArgumentException("No applications defined for env: " + env);
        }

        // Loop through apps and return the one matching appName
        for (Object o : apps) {
            JSONObject app = (JSONObject) o;
            if (appName.equals(app.optString("appName"))) {
                return app;
            }
        }

        throw new IllegalArgumentException("App not found: " + appName + " in env: " + env);
    }

    /**
     * Fetches the config block for a specific company under a given app + environment.
     *
     * @param env         Environment name (e.g., "test")
     * @param appName     Application name (e.g., "SBS")
     * @param companyCode Code of the company to look for (e.g., "VCX001")
     * @return JSONObject representing the selected company's config
     */
    public static JSONObject getCompanyConfig(String env, String appName, String companyCode) {
        JSONObject app = getAppConfig(env, appName);  // Get the app block
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

    /**
     * Fetches the config block for a specific user inside a company under app/env.
     *
     * @param env         Environment name (e.g., "test")
     * @param appName     Application name (e.g., "SBS")
     * @param companyCode Company code to search under
     * @param userId      The user ID to match (e.g., "qa.tester")
     * @return JSONObject representing the selected user's config
     */
    public static JSONObject getUserConfig(String env, String appName, String companyCode, String userId) {
        JSONObject comp = getCompanyConfig(env, appName, companyCode);  // First go to company level
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

    // ──────────────────────────────────────────────────────────────────────────────
    // SECTION: Properties File Fallback
    // ──────────────────────────────────────────────────────────────────────────────
    /**
     * Loads a simple key-value from config.properties in classpath.
     * Useful for fallback settings like `browser=chrome`, `timeout=30`
     *
     * @param key          The key to search for
     * @param defaultValue Value returned if key is missing or file not found
     * @return The value of the property, or default if not found
     */
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

    /**
     * Fetches the "apiBase" value for a given environment and application.
     *
     * @param env     The environment key (e.g., "test", "staging")
     * @param appName The application name to lookup under that environment
     * @return The apiBase URL as a string
     */
    public static String getApiBase(String env, String appName) {
        JSONObject appConfig = getAppConfig(env, appName); // Get the app block
        String apiBase = appConfig.optString("apiBase", null);

        if (apiBase == null || apiBase.isEmpty()) {
            throw new IllegalArgumentException("API Base URL is missing in config for app: " + appName + " in env: " + env);
        }

        return apiBase;
    }

    // ──────────────────────────────────────────────────────────────────────────────

    /**
     * Returns all top-level environment keys (e.g., "test", "prod", "dev").
     * Useful for running sanity checks or loading dropdowns.
     *
     * @return Set of environment keys defined in config.json
     */
    public static Set<String> getEnvironments() {
        return jsonConfig.keySet();
    }
}