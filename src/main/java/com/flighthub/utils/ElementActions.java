package com.flighthub.utils;
import org.openqa.selenium.JavascriptExecutor;
import com.flighthub.config.ConfigReader;
import com.flighthub.driver.DriverFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Wrapper utility class for common Selenium WebElement actions.
 * Provides explicit wait mechanisms, safe interactions, and
 * comprehensive logging for all element operations.
 */
public class ElementActions {

    private static final Logger logger = LogManager.getLogger(ElementActions.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    private static final int DEFAULT_TIMEOUT = config.getExplicitWait();

    private ElementActions() {
        // Utility class - prevent instantiation
    }

    // ===================== WAIT METHODS =====================

    /**
     * Create a WebDriverWait instance with specified timeout
     */
    public static WebDriverWait getWait(int timeoutInSeconds) {
        return new WebDriverWait(DriverFactory.getDriver(), Duration.ofSeconds(timeoutInSeconds));
    }

    public static WebDriverWait getWait() {
        return getWait(DEFAULT_TIMEOUT);
    }

    /**
     * Wait for element to be visible
     */
    public static WebElement waitForVisibility(By locator) {
        logger.debug("Waiting for visibility of element: {}", locator);
        return getWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(By locator, int timeout) {
        return getWait(timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static WebElement waitForVisibility(WebElement element) {
        logger.debug("Waiting for visibility of element");
        return getWait().until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Wait for element to be clickable
     */
    public static WebElement waitForClickable(By locator) {
        logger.debug("Waiting for element to be clickable: {}", locator);
        return getWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static WebElement waitForClickable(WebElement element) {
        return getWait().until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Wait for element to be present in DOM
     */
    public static WebElement waitForPresence(By locator) {
        logger.debug("Waiting for presence of element: {}", locator);
        return getWait().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    /**
     * Wait for element to be invisible
     */
    public static boolean waitForInvisibility(By locator) {
        logger.debug("Waiting for invisibility of element: {}", locator);
        return getWait().until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    /**
     * Wait for element to be invisible
     */
    public static boolean waitForInvisibility(WebElement element) {
        return getWait().until(ExpectedConditions.invisibilityOf(element));
    }

    // ===================== CLICK METHODS =====================

    /**
     * Safe click with wait and JavaScript fallback
     */
    public static void click(By locator) {
        try {
            WebElement element = waitForClickable(locator);
            element.click();
            logger.debug("Clicked element: {}", locator);
        } catch (ElementClickInterceptedException | TimeoutException e) {
            logger.warn("Regular click failed, trying JavaScript click for: {}", locator);
            clickByJavaScript(locator);
        }
    }

    public static void click(WebElement element) {
        try {
            waitForClickable(element).click();
            logger.debug("Clicked element");
        } catch (ElementClickInterceptedException e) {
            clickByJavaScript(element);
        }
    }

    /**
     * Click using JavaScript executor
     */
    public static void clickByJavaScript(By locator) {
        WebElement element = waitForPresence(locator);
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("arguments[0].click();", element);
        logger.debug("Clicked element via JavaScript: {}", locator);
    }

    public static void clickByJavaScript(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("arguments[0].click();", element);
        logger.debug("Clicked element via JavaScript");
    }

    /**
     * Double click on element
     */
    public static void doubleClick(By locator) {
        WebElement element = waitForVisibility(locator);
        Actions actions = new Actions(DriverFactory.getDriver());
        actions.doubleClick(element).perform();
        logger.debug("Double-clicked element: {}", locator);
    }

    // ===================== INPUT METHODS =====================

    /**
     * Type text into an input field
     */
    public static void type(By locator, String text) {
        WebElement element = waitForVisibility(locator);

        // Focus the input (JS focus for pointer-events: none inputs)
        try {
            element.click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
            js.executeScript("arguments[0].focus();", element);
        }

        // Clear using Ctrl+A + Delete (more reliable than .clear() for React)
        element.sendKeys(Keys.CONTROL, "a");
        element.sendKeys(Keys.DELETE);

        // Type the text
        element.sendKeys(text);

        // 🔥 CRITICAL: Dispatch input event so React registers the change
        JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
        js.executeScript(
                "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                        "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                element
        );

        logger.debug("Typed '{}' into element: {}", text, locator);
    }

    public static void type(WebElement element, String text) {
        waitForVisibility(element);
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
        logger.debug("Typed '{}' into element", text);
    }

    public static void typeSlowly(By locator, String text, long delayMs) {
        WebElement element = waitForVisibility(locator);

        // Focus the input
        try {
            element.click();
        } catch (Exception e) {
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();
            js.executeScript("arguments[0].focus();", element);
        }

        // Clear existing text
        element.sendKeys(Keys.CONTROL, "a");
        element.sendKeys(Keys.DELETE);

        // Type character by character with delay
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
            sleep(delayMs); // 100-200ms between keystrokes
        }

        logger.debug("Slowly typed '{}' into element: {}", text, locator);
    }

    /**
     * Type text using JavaScript (useful for react/angular inputs)
     */
    public static void typeByJavaScript(By locator, String text) {
        WebElement element = waitForPresence(locator);
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("arguments[0].value = arguments[1];", element, text);
        executor.executeScript("arguments[0].dispatchEvent(new Event('input', { bubbles: true }));", element);
        executor.executeScript("arguments[0].dispatchEvent(new Event('change', { bubbles: true }));", element);
        logger.debug("Typed '{}' via JavaScript into: {}", text, locator);
    }

    /**
     * Clear input field
     */
    public static void clear(By locator) {
        WebElement element = waitForVisibility(locator);
        element.clear();
        logger.debug("Cleared element: {}", locator);
    }

    // ===================== GET METHODS =====================

    /**
     * Get text from element
     */
    public static String getText(By locator) {
        String text = waitForVisibility(locator).getText();
        logger.debug("Got text from {}: '{}'", locator, text);
        return text;
    }

    public static String getText(WebElement element) {
        return waitForVisibility(element).getText();
    }

    /**
     * Get attribute value
     */
    public static String getAttribute(By locator, String attribute) {
        return waitForPresence(locator).getDomAttribute(attribute);
    }

    /**
     * Check if element is displayed
     */
    public static boolean isDisplayed(By locator) {
        try {
            return DriverFactory.getDriver().findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Check if element is displayed with wait
     */
    public static boolean isDisplayed(By locator, int timeout) {
        try {
            return waitForVisibility(locator, timeout).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Check if element is enabled
     */
    public static boolean isEnabled(By locator) {
        try {
            return waitForVisibility(locator).isEnabled();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Check if element exists in DOM
     */
    public static boolean isElementPresent(By locator) {
        try {
            DriverFactory.getDriver().findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ===================== SCROLLING METHODS =====================

    /**
     * Scroll to element
     */
    public static void scrollToElement(By locator) {
        WebElement element = waitForPresence(locator);
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
        logger.debug("Scrolled to element: {}", locator);
    }

    public static void scrollToElement(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    /**
     * Scroll to top of page
     */
    public static void scrollToTop() {
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("window.scrollTo(0, 0);");
    }

    /**
     * Scroll to bottom of page
     */
    public static void scrollToBottom() {
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        executor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    // ===================== DROPDOWN METHODS =====================

    /**
     * Select dropdown option by visible text
     */
    public static void selectByVisibleText(By locator, String text) {
        WebElement element = waitForClickable(locator);
        Select select = new Select(element);
        select.selectByVisibleText(text);
        logger.debug("Selected '{}' from dropdown: {}", text, locator);
    }

    /**
     * Select dropdown option by value
     */
    public static void selectByValue(By locator, String value) {
        WebElement element = waitForClickable(locator);
        Select select = new Select(element);
        select.selectByValue(value);
        logger.debug("Selected value '{}' from dropdown: {}", value, locator);
    }

    /**
     * Select dropdown option by index
     */
    public static void selectByIndex(By locator, int index) {
        WebElement element = waitForClickable(locator);
        Select select = new Select(element);
        select.selectByIndex(index);
        logger.debug("Selected index {} from dropdown: {}", index, locator);
    }

    // ===================== FRAME METHODS =====================

    /**
     * Switch to frame by index
     */
    public static void switchToFrame(int index) {
        DriverFactory.getDriver().switchTo().frame(index);
        logger.debug("Switched to frame index: {}", index);
    }

    /**
     * Switch to frame by locator
     */
    public static void switchToFrame(By locator) {
        WebElement frame = waitForPresence(locator);
        DriverFactory.getDriver().switchTo().frame(frame);
        logger.debug("Switched to frame: {}", locator);
    }

    /**
     * Switch to default content
     */
    public static void switchToDefaultContent() {
        DriverFactory.getDriver().switchTo().defaultContent();
        logger.debug("Switched to default content");
    }

    // ===================== WAIT/SLEEP METHODS =====================

    /**
     * Hard wait (use sparingly, prefer explicit waits)
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Sleep interrupted", e);
        }
    }

    /**
     * Wait for page to load completely
     */
    public static void waitForPageLoad() {
        WebDriverWait wait = getWait(config.getPageLoadTimeout());
        wait.until(driver -> ((JavascriptExecutor) driver)
                .executeScript("return document.readyState").equals("complete"));
        logger.debug("Page loaded completely");
    }

    // ===================== ALERT METHODS =====================

    /**
     * Accept alert
     */
    public static void acceptAlert() {
        getWait().until(ExpectedConditions.alertIsPresent());
        DriverFactory.getDriver().switchTo().alert().accept();
        logger.debug("Alert accepted");
    }

    /**
     * Dismiss alert
     */
    public static void dismissAlert() {
        getWait().until(ExpectedConditions.alertIsPresent());
        DriverFactory.getDriver().switchTo().alert().dismiss();
        logger.debug("Alert dismissed");
    }

    /**
     * Get alert text
     */
    public static String getAlertText() {
        getWait().until(ExpectedConditions.alertIsPresent());
        return DriverFactory.getDriver().switchTo().alert().getText();
    }

    // ===================== UTILITY METHODS =====================

    /**
     * Find elements
     */
    public static List<WebElement> findElements(By locator) {
        return DriverFactory.getDriver().findElements(locator);
    }

    /**
     * Hover over element
     */
    public static void hoverOver(By locator) {
        WebElement element = waitForVisibility(locator);
        Actions actions = new Actions(DriverFactory.getDriver());
        actions.moveToElement(element).perform();
        logger.debug("Hovered over element: {}", locator);
    }

    /**
     * Get page source
     */
    public static String getPageSource() {
        return DriverFactory.getDriver().getPageSource();
    }

    /**
     * Refresh page
     */
    public static void refreshPage() {
        DriverFactory.getDriver().navigate().refresh();
        logger.debug("Page refreshed");
        waitForPageLoad();
    }

    /**
     * Navigate back
     */
    public static void navigateBack() {
        DriverFactory.getDriver().navigate().back();
        logger.debug("Navigated back");
        waitForPageLoad();
    }

    /**
     * Execute JavaScript
     */
    public static Object executeJavaScript(String script, Object... args) {
        JavascriptExecutor executor = (JavascriptExecutor) DriverFactory.getDriver();
        return executor.executeScript(script, args);
    }

    public static void pressEscape() {
    }
}
