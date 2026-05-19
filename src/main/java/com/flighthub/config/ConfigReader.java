package com.flighthub.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton configuration reader that loads properties from config files
 * and environment variables. Supports overriding config values via
 * system properties for CI/CD integration.
 */
public class ConfigReader {

    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static final String CONFIG_FILE = "src/test/resources/config.properties";
    private static ConfigReader instance;
    private final Properties properties;

    private ConfigReader() {
        properties = new Properties();
        loadProperties();
    }

    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }

    /**
     * Load properties from config file, then override with system properties
     */
    private void loadProperties() {
        try (InputStream inputStream = new FileInputStream(CONFIG_FILE)) {
            properties.load(inputStream);
            logger.info("Configuration loaded from: {}", CONFIG_FILE);
        } catch (IOException e) {
            logger.warn("Could not load config file: {}. Using defaults.", CONFIG_FILE);
        }

        // Override with system properties (for CI/CD and command-line)
        properties.putAll(System.getProperties());
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public String getBaseUrl() {
        return getProperty("base.url", "https://www.flighthub.com");
    }

    public String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    public boolean isHeadless() {
        return Boolean.parseBoolean(getProperty("headless", "false"));
    }

    public String getExecutionMode() {
        return getProperty("execution.mode", "local").toLowerCase();
    }

    public String getHubUrl() {
        return getProperty("hub.url", "http://localhost:4444/wd/hub");
    }

    public int getImplicitWait() {
        return Integer.parseInt(getProperty("implicit.wait", "10"));
    }

    public int getExplicitWait() {
        return Integer.parseInt(getProperty("explicit.wait", "15"));
    }

    public int getPageLoadTimeout() {
        return Integer.parseInt(getProperty("page.load.timeout", "15"));
    }

    public boolean takeScreenshots() {
        return Boolean.parseBoolean(getProperty("screenshots.enabled", "true"));
    }

    public String getScreenshotPath() {
        return getProperty("screenshot.path", "screenshots/");
    }

    /**
     * Check if tests are running on Selenium Grid
     */
    public boolean isGridExecution() {
        return "grid".equalsIgnoreCase(getExecutionMode());
    }

    /**
     * Get the platform for Grid execution
     */
    public String getGridPlatform() {
        return getProperty("grid.platform", "LINUX");
    }

    public void reload() {
        properties.clear();
        loadProperties();
        logger.info("Configuration reloaded");
    }
}
