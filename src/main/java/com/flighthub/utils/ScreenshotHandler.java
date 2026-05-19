package com.flighthub.utils;

import com.flighthub.config.ConfigReader;
import com.flighthub.driver.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for capturing and managing screenshots during test execution.
 * Screenshots are saved with timestamps for easy identification.
 */
public class ScreenshotHandler {

    private static final Logger logger = LogManager.getLogger(ScreenshotHandler.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    private static final String SCREENSHOT_PATH = config.getScreenshotPath();

    static {
        // Create screenshots directory if it doesn't exist
        File directory = new File(SCREENSHOT_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.info("Created screenshot directory: {}", SCREENSHOT_PATH);
        }
    }

    private ScreenshotHandler() {
        // Utility class
    }

    /**
     * Take a screenshot and save it to the configured directory.
     *
     * @param testName Name of the test for the filename
     * @return Absolute path to the saved screenshot
     */
    public static String takeScreenshot(String testName) {
        if (!config.takeScreenshots()) {
            logger.debug("Screenshots are disabled in configuration");
            return null;
        }

        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                logger.warn("Cannot take screenshot - driver is null");
                return null;
            }

            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String sanitizedTestName = testName.replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = String.format("%s_%s.png", sanitizedTestName, timestamp);
            String filePath = SCREENSHOT_PATH + fileName;

            File destinationFile = new File(filePath);
            FileUtils.copyFile(sourceFile, destinationFile);

            logger.info("Screenshot saved: {}", destinationFile.getAbsolutePath());
            return destinationFile.getAbsolutePath();

        } catch (IOException e) {
            logger.error("Failed to save screenshot for test: {}", testName, e);
            return null;
        } catch (Exception e) {
            logger.error("Error taking screenshot for test: {}", testName, e);
            return null;
        }
    }

    /**
     * Take a screenshot on test failure with special naming
     */
    public static String takeFailureScreenshot(String testName) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String failureName = "FAILED_" + testName + "_" + timestamp;
        return takeScreenshot(failureName);
    }

    /**
     * Take a screenshot and return as Base64 string for embedding in reports
     */
    public static String getScreenshotAsBase64() {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                return null;
            }
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            return takesScreenshot.getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            logger.error("Failed to capture Base64 screenshot", e);
            return null;
        }
    }

    /**
     * Take a screenshot of the full page using JavaScript
     */
    public static String takeFullPageScreenshot(String testName) {
        try {
            WebDriver driver = DriverFactory.getDriver();
            if (driver == null) {
                return null;
            }

            // Execute JavaScript to get full page dimensions
            long fullWidth = (Long) ElementActions.executeJavaScript(
                    "return Math.max(document.body.scrollWidth, document.body.offsetWidth, " +
                    "document.documentElement.clientWidth, document.documentElement.scrollWidth, " +
                    "document.documentElement.offsetWidth);");
            long fullHeight = (Long) ElementActions.executeJavaScript(
                    "return Math.max(document.body.scrollHeight, document.body.offsetHeight, " +
                    "document.documentElement.clientHeight, document.documentElement.scrollHeight, " +
                    "document.documentElement.offsetHeight);");

            logger.debug("Full page dimensions: {}x{}", fullWidth, fullHeight);

            // Set window size to full page and take screenshot
            driver.manage().window().setSize(new org.openqa.selenium.Dimension((int) fullWidth, (int) fullHeight));
            ElementActions.sleep(500); // Wait for resize

            String result = takeScreenshot(testName + "_fullpage");

            // Restore window size
            driver.manage().window().maximize();

            return result;

        } catch (Exception e) {
            logger.error("Failed to take full page screenshot", e);
            return takeScreenshot(testName);
        }
    }

    /**
     * Clean up old screenshots older than specified days
     */
    public static void cleanupOldScreenshots(int daysToKeep) {
        File directory = new File(SCREENSHOT_PATH);
        if (!directory.exists() || !directory.isDirectory()) {
            return;
        }

        long cutoffTime = System.currentTimeMillis() - (daysToKeep * 24 * 60 * 60 * 1000L);
        File[] files = directory.listFiles();

        if (files != null) {
            int deletedCount = 0;
            for (File file : files) {
                if (file.isFile() && file.lastModified() < cutoffTime) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
            logger.info("Cleaned up {} old screenshot(s)", deletedCount);
        }
    }

    /**
     * Get the configured screenshot directory path
     */
    public static String getScreenshotDirectory() {
        return new File(SCREENSHOT_PATH).getAbsolutePath();
    }
}
