package com.Vcidex.StoryboardSystems.Utils.Config;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigLoader {
    private static ConfigLoader instance;
    private static final Logger logger = LoggerFactory.getLogger(ConfigLoader.class);
    private Properties properties = new Properties();

    private ConfigLoader(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input == null) {
                logger.error("Unable to find configuration file: " + configFile);
            } else {
                properties.load(input);
                logger.info("Configuration loaded successfully.");
            }
        } catch (IOException e) {
            logger.error("Failed to load configuration: " + e.getMessage(), e);
        }
    }

    public static ConfigLoader getInstance(String configFile) {
        if (instance == null) {
            instance = new ConfigLoader(configFile);
        }
        return instance;
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}