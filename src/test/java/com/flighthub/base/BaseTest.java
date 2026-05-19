package com.flighthub.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.flighthub.config.ConfigReader;
import com.flighthub.driver.DriverFactory;
import com.flighthub.utils.ScreenshotHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;
import com.aventstack.extentreports.Status;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base Test class that serves as the foundation for all test classes.
 * Provides:
 * - WebDriver initialization and cleanup
 * - ExtentReports reporting
 * - Screenshot capture on failure
 * - Test context and logging setup
 * - Browser configuration via TestNG parameters
 */
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    private static ExtentReports extentReports;
    private static ExtentSparkReporter sparkReporter;
    private ExtentTest extentTest;

    protected WebDriver driver;

    // ========== TEST SUITE SETUP ==========

    /**
     * One-time setup before the entire test suite runs.
     * Initializes the ExtentReports reporting engine.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        logger.info("========================================");
        logger.info("Test Suite Execution Starting");
        logger.info("Browser: {}", config.getBrowser());
        logger.info("Execution Mode: {}", config.getExecutionMode());
        logger.info("Base URL: {}", config.getBaseUrl());
        logger.info("========================================");

        // Initialize ExtentReports
        setupExtentReports();
    }

    /**
     * Clean up after the entire test suite
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("ExtentReports flushed and saved");
        }
        logger.info("========================================");
        logger.info("Test Suite Execution Completed");
        logger.info("========================================");
    }

    // ========== TEST CLASS SETUP ==========

    /**
     * Setup before each test class - set class name in report
     */
    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext context) {
        String className = this.getClass().getSimpleName();
        logger.info("Starting test class: {}", className);
    }

    @AfterClass(alwaysRun = true)
    public void afterClass() {
        String className = this.getClass().getSimpleName();
        logger.info("Completed test class: {}", className);
    }

    // ========== TEST METHOD SETUP ==========

    /**
     * Setup before each test method:
     * - Initialize WebDriver
     * - Create ExtentTest node
     * - Log test start
     */
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser", "executionMode", "hubUrl"})
    public void setUp(@Optional String browser,
                      @Optional String executionMode,
                      @Optional String hubUrl,
                      Method method,
                      ITestContext context) {
        // Set system properties from parameters if provided
        if (browser != null && !browser.isEmpty()) {
            System.setProperty("browser", browser);
        }
        if (executionMode != null && !executionMode.isEmpty()) {
            System.setProperty("execution.mode", executionMode);
        }
        if (hubUrl != null && !hubUrl.isEmpty()) {
            System.setProperty("hub.url", hubUrl);
        }

        // Reload config to pick up any system property overrides
        config.reload();

        // Initialize WebDriver
        driver = DriverFactory.initializeDriver();

        // Create ExtentTest node
        String testName = method.getName();
        String className = this.getClass().getSimpleName();
        extentTest = extentReports.createTest(testName)
                .assignCategory(className);

        // Log test method info
        logger.info("----------------------------------------");
        logger.info("Starting test: {}.{} | Browser: {}", className, testName, config.getBrowser());
        logger.info("Thread ID: {}", Thread.currentThread().getId());

        extentTest.info("Browser: " + config.getBrowser());
        extentTest.info("Execution Mode: " + config.getExecutionMode());
    }

    /**
     * Cleanup after each test method:
     * - Take screenshot on failure
     * - Log test result
     * - Quit WebDriver
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result, Method method) {
        String testName = method.getName();

        try {
            // Handle test result
            switch (result.getStatus()) {
                case ITestResult.SUCCESS:
                    logger.info("Test PASSED: {}", testName);
                    extentTest.pass("Test passed successfully");
                    break;

                case ITestResult.FAILURE:
                    logger.error("Test FAILED: {} - {}", testName, result.getThrowable());
                    extentTest.fail("Test failed: " + result.getThrowable());

                    // Take screenshot on failure
                    String screenshotPath = ScreenshotHandler.takeFailureScreenshot(testName);
                    if (screenshotPath != null) {
                        extentTest.addScreenCaptureFromPath(screenshotPath);
                        logger.info("Failure screenshot saved: {}", screenshotPath);
                    }

                    // Add screenshot as Base64 for embedded viewing
                    String base64Screenshot = ScreenshotHandler.getScreenshotAsBase64();
                    if (base64Screenshot != null) {
                        extentTest.addScreenCaptureFromBase64String(base64Screenshot);
                    }
                    break;

                case ITestResult.SKIP:
                    logger.warn("Test SKIPPED: {} - {}", testName, result.getThrowable());
                    extentTest.skip("Test skipped: " + result.getThrowable());
                    break;

                default:
                    logger.warn("Test status UNKNOWN: {} - Status: {}", testName, result.getStatus());
                    break;
            }

            // Log execution time
            long duration = result.getEndMillis() - result.getStartMillis();
            extentTest.info("Execution time: " + duration + "ms");

        } catch (Exception e) {
            logger.error("Error in tearDown: {}", e.getMessage(), e);
        } finally {
            // Always quit the driver
            DriverFactory.quitDriver();
            logger.info("Driver quit for test: {}", testName);
            logger.info("----------------------------------------");
        }
    }

    // ========== REPORTING METHODS ==========

    /**
     * Log an info message to both logger and ExtentReport
     */
    protected void logInfo(String message) {
        logger.info(message);
        if (extentTest != null) {
            extentTest.info(message);
        }
    }

    /**
     * Log a warning message
     */
    protected void logWarning(String message) {
        logger.warn(message);
        if (extentTest != null) {
            extentTest.warning(message);
        }
    }

    /**
     * Log an error message
     */
    protected void logError(String message) {
        logger.error(message);
        if (extentTest != null) {
            extentTest.log(Status.FAIL, message);
        }
    }

    /**
     * Log a pass message
     */
    protected void logPass(String message) {
        logger.info("PASS: {}", message);
        if (extentTest != null) {
            extentTest.log(Status.PASS, message);
        }
    }

    /**
     * Log a fail message
     */
    protected void logFail(String message) {
        logger.error("FAIL: {}", message);
        if (extentTest != null) {
            extentTest.fail(message);
        }
    }

    /**
     * Add a screenshot to the current test report
     */
    protected void addScreenshotToReport(String screenshotName) {
        String screenshotPath = ScreenshotHandler.takeScreenshot(screenshotName);
        if (screenshotPath != null && extentTest != null) {
            extentTest.addScreenCaptureFromPath(screenshotPath);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Get current page title
     */
    protected String getPageTitle() {
        return DriverFactory.getPageTitle();
    }

    /**
     * Get current page URL
     */
    protected String getCurrentUrl() {
        return DriverFactory.getCurrentUrl();
    }

    /**
     * Navigate to a specific URL
     */
    protected void navigateTo(String url) {
        driver.get(url);
        logger.info("Navigated to: {}", url);
    }

    /**
     * Sleep for specified milliseconds (use sparingly)
     */
    protected void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted");
        }
    }

    // ========== PRIVATE METHODS ==========

    /**
     * Initialize ExtentReports with Spark reporter
     */
    private void setupExtentReports() {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String reportPath = "target/reports/FlightHub_TestReport_" + timestamp + ".html";

            sparkReporter = new ExtentSparkReporter(reportPath);
            sparkReporter.config().setDocumentTitle("FlightHub Automation Test Report");
            sparkReporter.config().setReportName("FlightHub Regression Suite");
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
            sparkReporter.config().setEncoding("UTF-8");

            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);

            // System info
            extentReports.setSystemInfo("Project", "FlightHub Automation");
            extentReports.setSystemInfo("Environment", config.getExecutionMode().toUpperCase());
            extentReports.setSystemInfo("Browser", config.getBrowser());
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("Selenium Version", "4.18.1");
            extentReports.setSystemInfo("TestNG Version", "7.9.1");

            if (config.isGridExecution()) {
                extentReports.setSystemInfo("Grid Hub", config.getHubUrl());
            }

            logger.info("ExtentReports initialized. Report will be saved to: {}", reportPath);

        } catch (Exception e) {
            logger.error("Failed to initialize ExtentReports: {}", e.getMessage(), e);
        }
    }
}
