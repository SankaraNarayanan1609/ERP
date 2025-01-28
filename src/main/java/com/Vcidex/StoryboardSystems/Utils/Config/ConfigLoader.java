package com.Vcidex.StoryboardSystems.Utils.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private static final Properties properties = new Properties();

    public ConfigLoader(String s) {
    }

    public static void load(String configFile) {
        try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                logger.warn("Config file not found: {}", configFile);
                // Fallback to default properties if the file is not found
                loadDefaultConfig();
                return;
            }
            properties.load(input);
            logger.info("Loaded config file: {}", configFile);
        } catch (IOException e) {
            logger.error("Failed to load configuration: {}", configFile, e);
            loadDefaultConfig(); // Fallback to default properties on error
        }
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    private static void loadDefaultConfig() {
        // Load default config values in case of missing or failed configuration file
        properties.setProperty("defaultKey", "defaultValue");
        logger.info("Loaded default configuration values.");
    }
}
