package com.flighthub.listeners;

import com.flighthub.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.IAnnotationTransformer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * TestNG RetryAnalyzer that automatically retries failed tests.
 * Configurable retry count via config.properties or system property.
 *
 * To use retry on a specific test, add:
 * @Test(retryAnalyzer = RetryListener.class)
 */
public class RetryListener implements IRetryAnalyzer, IAnnotationTransformer {

    private static final Logger logger = LogManager.getLogger(RetryListener.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    private static final int MAX_RETRIES;

    static {
        int retries = 1;
        try {
            retries = Integer.parseInt(config.getProperty("retry.attempts", "1"));
        } catch (NumberFormatException e) {
            logger.warn("Invalid retry.attempts value, using default: 1");
        }
        MAX_RETRIES = retries;
    }

    private int retryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (!result.isSuccess() && retryCount < MAX_RETRIES) {
            retryCount++;
            logger.warn("Retrying test: {} (Attempt {}/{})",
                    result.getName(), retryCount, MAX_RETRIES);
            return true;
        }
        return false;
    }

    @Override
    public void transform(ITestAnnotation annotation, Class testClass,
                          Constructor testConstructor, Method testMethod) {
        // Set the retry analyzer for all tests
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryListener.class);
        }
    }
}
