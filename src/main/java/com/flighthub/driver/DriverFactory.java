package com.flighthub.driver;

import com.flighthub.config.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class responsible for creating and managing WebDriver instances.
 * Supports both local execution and Selenium Grid remote execution.
 * Implements ThreadLocal for thread-safe parallel test execution.
 */
public class DriverFactory {

    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();

    private DriverFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Initialize and return a WebDriver instance based on configuration.
     * Supports local and grid execution modes.
     */
    public static WebDriver initializeDriver() {
        String browser = config.getBrowser();
        String executionMode = config.getExecutionMode();

        logger.info("Initializing driver - Browser: {}, Execution Mode: {}", browser, executionMode);

        WebDriver webDriver;

        if ("grid".equalsIgnoreCase(executionMode)) {
            webDriver = createRemoteDriver(browser);
        } else {
            webDriver = createLocalDriver(browser);
        }

        // Configure timeouts
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
        webDriver.manage().timeouts().scriptTimeout(Duration.ofSeconds(config.getExplicitWait()));

        // Maximize window for local execution
        if (!config.isHeadless()) {
            webDriver.manage().window().maximize();
        }

        driver.set(webDriver);
        logger.info("Driver initialized successfully");
        return webDriver;
    }

    /**
     * Create a local WebDriver instance
     */
    private static WebDriver createLocalDriver(String browser) {
        switch (browser) {
            case "chrome":
                return createChromeDriver();
            case "firefox":
                return createFirefoxDriver();
            case "edge":
                return createEdgeDriver();
            case "safari":
                return createSafariDriver();
            default:
                logger.warn("Unknown browser '{}'. Defaulting to Chrome.", browser);
                return createChromeDriver();
        }
    }

    /**
     * Create ChromeDriver with options
     */
    private static WebDriver createChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");

        // Experimental options
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.cookies", 1);
        prefs.put("profile.cookie_controls_mode", 0);
        options.setExperimentalOption("prefs", prefs);

        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
        options.setExperimentalOption("useAutomationExtension", false);

        logger.info("Chrome driver created with options");
        return new ChromeDriver(options);
    }

    /**
     * Create FirefoxDriver with options
     */
    private static WebDriver createFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless");
        }

        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        options.addPreference("dom.webnotifications.enabled", false);

        logger.info("Firefox driver created with options");
        return new FirefoxDriver(options);
    }

    /**
     * Create EdgeDriver with options
     */
    private static WebDriver createEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();

        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }

        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-notifications");

        logger.info("Edge driver created with options");
        return new EdgeDriver(options);
    }

    /**
     * Create SafariDriver with options
     */
    private static WebDriver createSafariDriver() {
        SafariOptions options = new SafariOptions();
        options.setAutomaticInspection(false);
        logger.info("Safari driver created");
        return new SafariDriver(options);
    }

    /**
     * Create a RemoteWebDriver for Selenium Grid execution
     */
    private static WebDriver createRemoteDriver(String browser) {
        String hubUrl = config.getHubUrl();
        logger.info("Creating remote driver for Grid at: {}", hubUrl);

        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setBrowserName(browser);
            capabilities.setCapability("platformName", config.getGridPlatform());

            // Browser-specific options for Grid
            switch (browser.toLowerCase()) {
                case "chrome":
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--no-sandbox");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--window-size=1920,1080");
                    chromeOptions.merge(capabilities);
                    return new RemoteWebDriver(new URL("http://localhost:4444/"), chromeOptions);

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.addArguments("--width=1920");
                    firefoxOptions.addArguments("--height=1080");
                    firefoxOptions.merge(capabilities);
                    return new RemoteWebDriver(new URL("http://localhost:4444/"), firefoxOptions);

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--window-size=1920,1080");
                    edgeOptions.merge(capabilities);
                    return new RemoteWebDriver(new URL("http://localhost:4444/"), edgeOptions);

                default:
                    return new RemoteWebDriver(new URL("http://localhost:4444/"), capabilities);
            }
        } catch (MalformedURLException e) {
            logger.error("Invalid Grid Hub URL: {}", hubUrl, e);
            throw new RuntimeException("Invalid Selenium Grid Hub URL: " + hubUrl, e);
        }
    }

    /**
     * Get the current thread's WebDriver instance
     */
    public static WebDriver getDriver() {
        return driver.get();
    }

    /**
     * Check if a driver exists for the current thread
     */
    public static boolean hasDriver() {
        return driver.get() != null;
    }

    /**
     * Quit the current thread's driver and remove it
     */
    public static void quitDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            try {
                webDriver.quit();
                logger.info("Driver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting driver", e);
            } finally {
                driver.remove();
            }
        }
    }

    /**
     * Navigate to the base URL
     */
    public static void navigateToBaseUrl() {
        String baseUrl = config.getBaseUrl();
        getDriver().get(baseUrl);
        logger.info("Navigated to: {}", baseUrl);
    }

    /**
     * Get the current page title
     */
    public static String getPageTitle() {
        return getDriver().getTitle();
    }

    /**
     * Get the current page URL
     */
    public static String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
}
