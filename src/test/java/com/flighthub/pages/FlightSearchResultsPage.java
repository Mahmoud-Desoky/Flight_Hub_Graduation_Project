package com.flighthub.pages;

import com.flighthub.driver.DriverFactory;
import com.flighthub.utils.ElementActions;
import com.flighthub.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object class for the FlightHub Flight Search Results page.
 * Handles all interactions with the search results including
 * filtering, sorting, flight selection, pagination, and booking actions.
 */
public class FlightSearchResultsPage {

    private static final Logger logger = LogManager.getLogger(FlightSearchResultsPage.class);

    // ========== LOCATORS ==========

    // Page Header
    private final By resultsHeader = By.xpath("//h1 | //h2[contains(@class, 'results')] | //div[contains(@class, 'search-results-header')]");
    private final By searchSummary = By.xpath("//div[contains(@class, 'route-summary')] | //div[contains(@class, 'search-summary')]");
    private final By modifySearchButton = By.xpath("//button[contains(text(), 'Modify')] | //a[contains(text(), 'Modify')]");
    private final By backToHomeLink = By.xpath("//a[contains(@href, '/')]");

    // Loading State
    private final By loadingSpinner = By.cssSelector("[class*='spinner'], [class*='loading'], [class*='skeleton'], .loading-overlay");
    private final By noResultsMessage = By.xpath("//*[contains(text(), 'No flights') or contains(text(), 'not found') or contains(text(), 'Sorry')]");
    private final By errorMessage = By.xpath("//*[contains(@class, 'error') or contains(@class, 'alert')]");

    // Flight Results
    private final By flightResultCards = By.cssSelector("[data-testid*='flight-result'], [class*='flight-card'], [class*='result-card'], [class*='itinerary']");
    private final By flightPrices = By.cssSelector("[class*='price']:not([class*='filter']), [data-testid*='price']");
    private final By airlineNames = By.cssSelector("[class*='airline'] img, [class*='carrier'], img[alt*='Airlines'], img[alt*='Air']");
    private final By flightDurations = By.cssSelector("[class*='duration'], [class*='flight-time']");
    private final By departureTimes = By.cssSelector("[class*='departure']");
    private final By arrivalTimes = By.cssSelector("[class*='arrival']");

    // Filter Section
    private final By stopsFilterSection = By.xpath("//*[contains(text(), 'Stops')]");
    private final By nonStopFilter = By.xpath("//label[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'non-stop')] | //input[@value='0']/following-sibling::*");
    private final By oneStopFilter = By.xpath("//label[contains(text(), '1 stop')] | //input[@value='1']/following-sibling::*");
    private final By twoPlusStopsFilter = By.xpath("//label[contains(text(), '2+ stops')] | //input[@value='2']/following-sibling::*");

    private final By priceFilterSection = By.xpath("//*[contains(text(), 'Price')]");
    private final By priceRangeMin = By.cssSelector("input[placeholder*='Min'], input[placeholder*='From']");
    private final By priceRangeMax = By.cssSelector("input[placeholder*='Max'], input[placeholder*='To']");
    private final By applyPriceFilterButton = By.xpath("//button[contains(text(), 'Apply')]");

    private final By airlineFilterSection = By.xpath("//*[contains(text(), 'Airline')]");
    private final By airlineFilterCheckboxes = By.cssSelector("[class*='airline-filter'] input[type='checkbox'], [class*='filter-airline'] input");

    private final By departureTimeFilter = By.xpath("//*[contains(text(), 'Departure time')]");
    private final By arrivalTimeFilter = By.xpath("//*[contains(text(), 'Arrival time')]");

    // Sort Options
    private final By sortDropdown = By.cssSelector("[class*='sort'] select, button[class*='sort'], [class*='sort-dropdown']");
    private final By sortByPriceLowest = By.xpath("//option[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'lowest')] | //*[contains(text(), 'Lowest price')]");
    private final By sortByPriceHighest = By.xpath("//option[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'highest')]");
    private final By sortByDurationShortest = By.xpath("//option[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'shortest')] | //*[contains(text(), 'Duration')]");
    private final By sortByDepartureEarliest = By.xpath("//option[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'earliest')]");

    // Flight Details
    private final By selectFlightButton = By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'select')] | //a[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'select')] | //button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'choose')]");
    private final By flightDetailsButton = By.xpath("//button[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'details')] | //*[contains(text(), 'Flight details')]");
    private final By fareTypeOptions = By.cssSelector("[class*='fare-type'], [class*='fare-option']");

    // Pagination
    private final By nextPageButton = By.xpath("//button[contains(text(), 'Next')] | //a[contains(text(), 'Next')]");
    private final By previousPageButton = By.xpath("//button[contains(text(), 'Previous')] | //a[contains(text(), 'Previous')]");
    private final By pageNumbers = By.cssSelector("[class*='pagination'] button, [class*='pagination'] a");

    // ========== CONSTRUCTOR ==========

    public FlightSearchResultsPage() {
        logger.info("FlightSearchResultsPage object initialized");
    }

    // ========== PAGE STATE METHODS ==========

    /**
     * Wait for search results to fully load
     */
    public FlightSearchResultsPage waitForResultsToLoad() {
        // Positive wait: poll for results to appear instead of waiting for spinner to vanish
        boolean resultsLoaded = false;
        for (int i = 0; i < 30; i++) {  // 30 × 500ms = 15 seconds max
            if (hasResults() || isNoResultsDisplayed() || isErrorDisplayed()) {
                resultsLoaded = true;
                break;
            }
            ElementActions.sleep(500);
        }
        logger.info("Search results loaded: {}", resultsLoaded);
        return this;
    }

    /**
     * Check if the results page is loaded
     */
    public boolean isResultsPageLoaded() {
        String currentUrl = DriverFactory.getCurrentUrl();
        boolean hasResults = !getFlightResultElements().isEmpty();
        boolean isResultsUrl = currentUrl.contains("results") || currentUrl.contains("search");
        boolean isLoaded = isResultsUrl || hasResults || isSearchSummaryDisplayed();
        logger.info("Results page loaded: {} (URL: {}, results found: {})", isLoaded, currentUrl, hasResults);
        return isLoaded;
    }

    /**
     * Check if search summary is displayed
     */
    public boolean isSearchSummaryDisplayed() {
        return ElementActions.isDisplayed(searchSummary, 5);
    }

    /**
     * Check if there are no results
     */
    public boolean isNoResultsDisplayed() {
        return ElementActions.isDisplayed(noResultsMessage, 5);
    }

    /**
     * Check if an error message is displayed
     */
    public boolean isErrorDisplayed() {
        return ElementActions.isDisplayed(errorMessage, 3);
    }

    /**
     * Get error message text
     */
    public String getErrorMessage() {
        try {
            return ElementActions.getText(errorMessage);
        } catch (Exception e) {
            return "";
        }
    }

    // ========== RESULTS INFORMATION ==========

    /**
     * Get number of flight result cards displayed
     */
    public int getResultsCount() {
        List<WebElement> results = getFlightResultElements();
        int count = results.size();
        logger.info("Number of flight results displayed: {}", count);
        return count;
    }

    /**
     * Check if any results are present
     */
    public boolean hasResults() {
        return getResultsCount() > 0;
    }

    /**
     * Get the search summary/route text
     */
    public String getSearchSummary() {
        try {
            return ElementActions.getText(searchSummary);
        } catch (Exception e) {
            logger.warn("Could not retrieve search summary");
            return "";
        }
    }

    /**
     * Get price of the first result
     */
    public String getFirstResultPrice() {
        try {
            List<WebElement> prices = ElementActions.findElements(flightPrices);
            if (!prices.isEmpty()) {
                String price = prices.get(0).getText();
                logger.info("First result price: {}", price);
                return price;
            }
        } catch (Exception e) {
            logger.warn("Could not get first result price: {}", e.getMessage());
        }
        return "";
    }

    /**
     * Get all displayed prices
     */
    public List<String> getAllPrices() {
        List<WebElement> priceElements = ElementActions.findElements(flightPrices);
        List<String> prices = priceElements.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
        logger.info("Found {} price elements", prices.size());
        return prices;
    }

    /**
     * Get airline names from results
     */
    public List<String> getAirlineNames() {
        List<WebElement> airlines = ElementActions.findElements(airlineNames);
        List<String> names = airlines.stream()
                .map(el -> el.getAttribute("alt") != null ? el.getAttribute("alt") : el.getText())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        logger.info("Found {} airlines: {}", names.size(), names);
        return names;
    }

    /**
     * Get departure times from results
     */
    public List<String> getDepartureTimes() {
        List<WebElement> times = ElementActions.findElements(departureTimes);
        return times.stream()
                .map(WebElement::getText)
                .collect(Collectors.toList());
    }

    // ========== FILTERING METHODS ==========

    /**
     * Filter by non-stop flights
     */
    public FlightSearchResultsPage filterByNonStop() {
        try {
            ElementActions.click(nonStopFilter);
            ElementActions.sleep(200);
            logger.info("Applied Non-stop filter");
        } catch (Exception e) {
            logger.warn("Could not apply non-stop filter: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Filter by 1 stop flights
     */
    public FlightSearchResultsPage filterByOneStop() {
        try {
            ElementActions.click(oneStopFilter);
            ElementActions.sleep(2000);
            logger.info("Applied 1 stop filter");
        } catch (Exception e) {
            logger.warn("Could not apply 1 stop filter: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Filter by 2+ stops
     */
    public FlightSearchResultsPage filterByTwoPlusStops() {
        try {
            ElementActions.click(twoPlusStopsFilter);
            ElementActions.sleep(2000);
            logger.info("Applied 2+ stops filter");
        } catch (Exception e) {
            logger.warn("Could not apply 2+ stops filter: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Filter by maximum price
     */
    public FlightSearchResultsPage filterByMaxPrice(String maxPrice) {
        try {
            ElementActions.scrollToElement(priceFilterSection);
            ElementActions.type(priceRangeMax, maxPrice);
            ElementActions.click(applyPriceFilterButton);
            ElementActions.sleep(2000);
            logger.info("Applied max price filter: {}", maxPrice);
        } catch (Exception e) {
            logger.warn("Could not apply price filter: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Filter by a specific airline
     */
    public FlightSearchResultsPage filterByAirline(String airlineName) {
        try {
            ElementActions.scrollToElement(airlineFilterSection);
            By airlineCheckbox = By.xpath("//label[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                    + airlineName.toLowerCase() + "')]/preceding-sibling::input | //*[contains(text(), '"
                    + airlineName + "')]/preceding-sibling::*//input");
            if (ElementActions.isDisplayed(airlineCheckbox, 3)) {
                ElementActions.click(airlineCheckbox);
                ElementActions.sleep(2000);
                logger.info("Applied airline filter: {}", airlineName);
            }
        } catch (Exception e) {
            logger.warn("Could not apply airline filter '{}': {}", airlineName, e.getMessage());
        }
        return this;
    }

    // ========== SORTING METHODS ==========

    /**
     * Sort results by lowest price
     */
    public FlightSearchResultsPage sortByLowestPrice() {
        try {
            ElementActions.click(sortDropdown);
            ElementActions.sleep(500);
            ElementActions.click(sortByPriceLowest);
            ElementActions.sleep(2000);
            logger.info("Sorted by lowest price");
        } catch (Exception e) {
            logger.warn("Could not sort by lowest price: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Sort results by highest price
     */
    public FlightSearchResultsPage sortByHighestPrice() {
        try {
            ElementActions.click(sortDropdown);
            ElementActions.sleep(500);
            ElementActions.click(sortByPriceHighest);
            ElementActions.sleep(2000);
            logger.info("Sorted by highest price");
        } catch (Exception e) {
            logger.warn("Could not sort by highest price: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Sort results by shortest duration
     */
    public FlightSearchResultsPage sortByShortestDuration() {
        try {
            ElementActions.click(sortDropdown);
            ElementActions.sleep(500);
            ElementActions.click(sortByDurationShortest);
            ElementActions.sleep(2000);
            logger.info("Sorted by shortest duration");
        } catch (Exception e) {
            logger.warn("Could not sort by duration: {}", e.getMessage());
        }
        return this;
    }

    // ========== FLIGHT SELECTION METHODS ==========

    /**
     * Select the first flight result
     */
    public FlightSearchResultsPage selectFirstFlight() {
        try {
            List<WebElement> selectButtons = ElementActions.findElements(selectFlightButton);
            if (!selectButtons.isEmpty()) {
                ElementActions.click(selectButtons.get(0));
                logger.info("Selected first flight");
            } else {
                logger.warn("No select buttons found on results page");
            }
        } catch (Exception e) {
            logger.error("Could not select first flight: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Select flight at a specific index
     */
    public FlightSearchResultsPage selectFlightAtIndex(int index) {
        try {
            List<WebElement> selectButtons = ElementActions.findElements(selectFlightButton);
            if (index < selectButtons.size()) {
                ElementActions.click(selectButtons.get(index));
                ElementActions.sleep(1500);
                logger.info("Selected flight at index: {}", index);
            } else {
                logger.warn("Index {} out of range. Only {} flights available.", index, selectButtons.size());
            }
        } catch (Exception e) {
            logger.error("Could not select flight at index {}: {}", index, e.getMessage());
        }
        return this;
    }

    /**
     * View details of the first flight
     */
    public FlightSearchResultsPage viewFirstFlightDetails() {
        try {
            List<WebElement> detailButtons = ElementActions.findElements(flightDetailsButton);
            if (!detailButtons.isEmpty()) {
                ElementActions.click(detailButtons.get(0));
                ElementActions.sleep(1000);
                logger.info("Viewed first flight details");
            }
        } catch (Exception e) {
            logger.warn("Could not view flight details: {}", e.getMessage());
        }
        return this;
    }

    // ========== BOOKING NAVIGATION METHODS ==========




    /**
     * Click Modify Search to go back to home page
     */
    public HomePage clickModifySearch() {
        ElementActions.click(modifySearchButton);
        ElementActions.sleep(1000);
        logger.info("Clicked Modify Search");
        return new HomePage();
    }

    // ========== PAGINATION METHODS ==========

    /**
     * Go to next page of results
     */
    public FlightSearchResultsPage goToNextPage() {
        try {
            if (ElementActions.isDisplayed(nextPageButton, 3)) {
                ElementActions.click(nextPageButton);
                waitForResultsToLoad();
                logger.info("Navigated to next page");
            } else {
                logger.info("No next page button available");
            }
        } catch (Exception e) {
            logger.warn("Could not navigate to next page: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Go to previous page of results
     */
    public FlightSearchResultsPage goToPreviousPage() {
        try {
            if (ElementActions.isDisplayed(previousPageButton, 3)) {
                ElementActions.click(previousPageButton);
                waitForResultsToLoad();
                logger.info("Navigated to previous page");
            }
        } catch (Exception e) {
            logger.warn("Could not navigate to previous page: {}", e.getMessage());
        }
        return this;
    }

    // ========== PRIVATE HELPER METHODS ==========

    private List<WebElement> getFlightResultElements() {
        return ElementActions.findElements(flightResultCards);
    }
}