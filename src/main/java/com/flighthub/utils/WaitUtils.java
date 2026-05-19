package com.flighthub.utils;

import com.flighthub.config.ConfigReader;
import com.flighthub.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

/**
 * Advanced wait utility class providing custom wait conditions
 * and fluent wait mechanisms beyond standard explicit waits.
 */
public class WaitUtils {

    private static final Logger logger = LogManager.getLogger(WaitUtils.class);
    private static final ConfigReader config = ConfigReader.getInstance();

    private WaitUtils() {
        // Utility class
    }

    /**
     *
     * Fluent wait that allows custom polling interval
     */
    public static WebDriverWait fluentWait(int timeoutSeconds, int pollingMillis) {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds)) {
            {
                pollingEvery(Duration.ofMillis(pollingMillis));
            }
        };
    }

    /**
     * Wait for all Ajax requests to complete (jQuery)
     */
    public static boolean waitForAjaxToComplete() {
        return waitForAjaxToComplete(config.getExplicitWait());
    }

    public static boolean waitForAjaxToComplete(int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(driver -> {
            try {
                Boolean ajaxComplete = (Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return (typeof jQuery !== 'undefined') ? jQuery.active == 0 : true");
                return ajaxComplete != null && ajaxComplete;
            } catch (Exception e) {
                return true; // If jQuery is not present, consider AJAX complete
            }
        });
    }

    /**
     * Wait for all JavaScript animations to complete
     */
    public static boolean waitForJavaScriptAnimations() {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(config.getExplicitWait()));
        return wait.until(driver -> {
            Boolean animationsComplete = (Boolean) ((JavascriptExecutor) driver)
                    .executeScript("return document.getAnimations().length === 0");
            return animationsComplete != null && animationsComplete;
        });
    }

    /**
     * Wait for an element's text to contain specific value
     */
    public static boolean waitForTextToBePresentInElement(By locator, String text) {
        logger.debug("Waiting for text '{}' in element: {}", text, locator);
        return ElementActions.getWait().until(
                ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    /**
     * Wait for an element's attribute to contain specific value
     */
    public static boolean waitForAttributeToContain(By locator, String attribute, String value) {
        WebDriverWait wait = ElementActions.getWait();
        return wait.until(driver -> {
            try {
                WebElement element = driver.findElement(locator);
                String attrValue = element.getDomAttribute(attribute);
                return attrValue != null && attrValue.contains(value);
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * Wait for URL to contain specific text
     */
    public static boolean waitForUrlToContain(String urlPart) {
        logger.debug("Waiting for URL to contain: {}", urlPart);
        return ElementActions.getWait().until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * Wait for URL to match specific pattern
     */
    public static boolean waitForUrlToMatch(String regex) {
        logger.debug("Waiting for URL to match: {}", regex);
        return ElementActions.getWait().until(ExpectedConditions.urlMatches(regex));
    }

    /**
     * Wait for title to contain specific text
     */
    public static boolean waitForTitleToContain(String titlePart) {
        logger.debug("Waiting for title to contain: {}", titlePart);
        return ElementActions.getWait().until(ExpectedConditions.titleContains(titlePart));
    }

    /**
     * Wait for number of elements to be at least a specific count
     */
    public static boolean waitForMinimumElementCount(By locator, int minimumCount) {
        WebDriverWait wait = ElementActions.getWait();
        return wait.until(driver -> {
            List<WebElement> elements = driver.findElements(locator);
            return elements.size() >= minimumCount;
        });
    }

    /**
     * Wait for element count to equal specific number
     */
    public static boolean waitForElementCount(By locator, int expectedCount) {
        WebDriverWait wait = ElementActions.getWait();
        return wait.until(driver -> {
            List<WebElement> elements = driver.findElements(locator);
            return elements.size() == expectedCount;
        });
    }

    /**
     * Wait for an element to be stale (removed from DOM)
     */
    public static boolean waitForElementToBeStale(WebElement element) {
        WebDriverWait wait = ElementActions.getWait();
        return wait.until(ExpectedConditions.stalenessOf(element));
    }

    /**
     * Wait for a modal/dialog to appear
     */
    public static WebElement waitForModal(By modalLocator) {
        logger.debug("Waiting for modal: {}", modalLocator);
        WebElement modal = ElementActions.waitForVisibility(modalLocator);
        // Additional wait for modal animation
        ElementActions.sleep(300);
        return modal;
    }

    /**
     * Wait for a modal/dialog to disappear
     */
    public static boolean waitForModalToClose(By modalLocator) {
        logger.debug("Waiting for modal to close: {}", modalLocator);
        return ElementActions.waitForInvisibility(modalLocator);
    }

    /**
     * Custom wait with a specific condition
     */
    public static <T> T waitForCondition(Function<WebDriver, T> condition, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(condition::apply);
    }

    /**
     * Wait for spinner/loading indicator to disappear
     */
    public static boolean waitForSpinnerToDisappear(By spinnerLocator) {
        logger.debug("Waiting for spinner to disappear: {}", spinnerLocator);
        try {
            return ElementActions.waitForInvisibility(spinnerLocator);
        } catch (Exception e) {
            // Spinner might not be present, that's okay
            logger.debug("Spinner was not present or already gone");
            return true;
        }
    }

    /**
     * Wait for element to be clickable with custom timeout
     */
    public static WebElement waitForElementToBeClickable(By locator, int timeoutSeconds) {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutSeconds));
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Wait for new window/tab to be available
     */
    public static boolean waitForNewWindow(int expectedWindowCount) {
        WebDriverWait wait = ElementActions.getWait();
        return wait.until(ExpectedConditions.numberOfWindowsToBe(expectedWindowCount));
    }

    /**
     * Wait for page to fully load including all resources
     */
    public static void waitForPageToFullyLoad() {
        WebDriverWait wait = new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(config.getPageLoadTimeout()));
        
        // Wait for document ready state
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));

        // Wait for jQuery (if present)
        wait.until(driver -> {
            try {
                return (Boolean) ((JavascriptExecutor) driver)
                        .executeScript("return (typeof jQuery !== 'undefined') ? jQuery.active == 0 : true");
            } catch (Exception e) {
                return true;
            }
        });

        logger.debug("Page fully loaded");
    }

    /**
     * Retry an action with wait between attempts
     */
    public static boolean retryWithWait(Runnable action, int maxAttempts, long waitBetweenMillis) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                action.run();
                return true;
            } catch (Exception e) {
                logger.warn("Attempt {}/{} failed: {}", attempt, maxAttempts, e.getMessage());
                if (attempt < maxAttempts) {
                    ElementActions.sleep(waitBetweenMillis);
                }
            }
        }
        return false;
    }
}
