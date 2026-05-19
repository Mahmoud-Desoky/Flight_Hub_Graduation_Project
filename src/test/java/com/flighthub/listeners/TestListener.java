package com.flighthub.listeners;

import com.flighthub.config.ConfigReader;
import com.flighthub.driver.DriverFactory;
import com.flighthub.utils.ScreenshotHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG TestListener that provides enhanced logging and reporting capabilities.
 * Captures screenshots on failure and logs detailed test execution information.
 */
public class TestListener implements ITestListener {

    private static final Logger logger = LogManager.getLogger(TestListener.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    @Override
    public void onStart(ITestContext context) {
        logger.info("========================================");
        logger.info("Test Suite Started: {}", context.getName());
        logger.info("Total Tests: {}", context.getAllTestMethods().length);
        logger.info("Browser: {}", config.getBrowser());
        logger.info("Execution Mode: {}", config.getExecutionMode());
        logger.info("========================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        logger.info("========================================");
        logger.info("Test Suite Finished: {}", context.getName());
        logger.info("Passed: {}", context.getPassedTests().size());
        logger.info("Failed: {}", context.getFailedTests().size());
        logger.info("Skipped: {}", context.getSkippedTests().size());
        logger.info("Success Rate: {}%",
                context.getAllTestMethods().length > 0
                        ? (context.getPassedTests().size() * 100 / context.getAllTestMethods().length)
                        : 0);
        logger.info("========================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        logger.info("[START] {}.{} | Thread: {}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName(),
                Thread.currentThread().getId());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        logger.info("[PASS] {}.{} | Time: {}ms",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName(),
                result.getEndMillis() - result.getStartMillis());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        logger.error("[FAIL] {}.{} | Time: {}ms",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName(),
                result.getEndMillis() - result.getStartMillis(),
                result.getThrowable());

        // Take screenshot on failure
        if (DriverFactory.hasDriver()) {
            String screenshotPath = ScreenshotHandler.takeFailureScreenshot(result.getName());
            if (screenshotPath != null) {
                logger.info("Failure screenshot: {}", screenshotPath);
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        logger.warn("[SKIP] {}.{} | Reason: {}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "Unknown");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.warn("[PARTIAL] {}.{} - Failed within success percentage",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getName());
    }
}
