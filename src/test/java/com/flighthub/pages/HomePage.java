package com.flighthub.pages;

import com.flighthub.driver.DriverFactory;
import com.flighthub.utils.ElementActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;



import java.time.Duration;
import java.util.List;

/**
 * Page Object class for the FlightHub Home Page.
 * Encapsulates all elements and actions on the homepage including
 * the main search form, navigation, sign-in modal, and trip type selection.
 *
 * NOTE TO USER: This file contains ALL your existing login/register code
 * PLUS the new flight search methods needed for FlightSearchTests.
 * Nothing from your original file was removed.
 */
public class HomePage {

    private static final Logger logger = LogManager.getLogger(HomePage.class);

    // ========== LOCATORS ==========

    // Navigation
    private final By logo = By.cssSelector("a[href='https://www.flighthub.com/'] img");
    private final By supportLink = By.linkText("Support");
    private final By myTripsLink = By.linkText("My Trips");
    private final By signInButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[1]/div/div/div[2]/div[4]");
    private final By currencySelector = By.xpath("//*[@id=\"home-top-section\"]/div/div[1]/div/div/div[2]/div[4]");

    // Cookie Banner
    private final By cookieBanner = By.cssSelector("button + div");
    private final By acceptAllCookiesButton = By.xpath("//button[contains(text(), 'Accept All')]");
    private final By rejectNonEssentialCookiesButton = By.xpath("//button[contains(text(), 'Reject Non-Essential')]");
    private final By saveCookieSettingsButton = By.xpath("//button[contains(text(), 'Save')]");

    // Search Form - Tabs
    private final By flightsTab = By.xpath("//div[contains(text(), 'Flights')]");
    private final By flightsHotelTab = By.xpath("//div[contains(text(), 'Flights + Hotel')]");
    private final By hotelsTab = By.xpath("//div[contains(text(), 'Hotels')]");
    private final By carsTab = By.xpath("//div[contains(text(), 'Cars')]");
    private final By cruisesTab = By.xpath("//div[contains(text(), 'Cruises')]");
    private final By guidedTripsTab = By.xpath("//div[contains(text(), 'Guided Trips')]");

    // Search Form - Trip Type
    private final By roundTripOption = By.xpath("//div[contains(text(), 'Round Trip')] | //*[contains(@class, 'trip-type-roundtrip')]");
    private final By oneWayOption = By.xpath("//div[contains(text(), 'One Way')] | //*[contains(@class, 'trip-type-oneway')]");
    private final By multiCityOption = By.xpath("//div[contains(text(), 'Multi-City')] | //*[contains(@class, 'trip-type-multi')]");
    private final By economyDropdown = By.xpath("//div[contains(text(), 'Economy')]");

    // Search Form - Flight Fields (Round Trip)
    private final By leavingFromInput = By.id("seg0_from_display");
    private final By leavingFromField = By.xpath("//input[@id='seg0_from_display']/parent::div");
    private final By goingToInput = By.id("seg0_to_display");
    private final By goingToField = By.xpath("//input[@id='seg0_to_display']/parent::div");;
    private final By departingDateField = By.xpath("//label[contains(text(), 'Departing')]/following-sibling::div | //div[@id='seg0_date']");
    private final By returningDateField = By.xpath("//label[contains(text(), 'Returning')]/following-sibling::div | //div[@id='seg1_date']");
    private final By passengersDropdown = By.xpath("//label[contains(text(), 'Passenger')]/following-sibling::div");
    private final By searchButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[4]/form/div[2]/div/div[2]/div[3]");

    // Location autocomplete dropdown
    private final By locationDropdownFirstOption = By.cssSelector("ul li:first-child");
    private final By locationDropdownOptions = By.cssSelector("ul li");

    // Date picker (calendar)
    private final By datePickerDialog = By.cssSelector("[class*='datepicker'], [class*='calendar'], [role='dialog']");
    private final By datePickerNextMonthButton = By.cssSelector("[aria-label='Next Month'], button[class*='next'], [class*='chevron-right']");
    private final By datePickerPrevMonthButton = By.cssSelector("[aria-label='Previous Month'], button[class*='prev'], [class*='chevron-left']");
    private final By datePickerDayCell = By.cssSelector("[class*='day']:not([class*='disabled']):not([class*='outside'])");
    private final By datePickerSetDatesButton = By.xpath("//button[contains(text(), 'Set dates')]");

    // Multi-city
    private final By addCityButton = By.xpath("//*[contains(text(), 'Add city') or contains(text(), 'Add airport') or contains(@class, 'add-city')]");
    private final By removeLegButton = By.xpath("//button[contains(@class, 'remove') or contains(@class, 'delete')]");

    // Passenger dropdown controls
    private final By passengerDropdownTrigger = By.xpath("//div[contains(@class, 'passenger-input-wrapper')] | //input[@name='pax'] | //*[contains(text(), 'Passenger')]");
    private final By adultIncrementButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[4]/form/div[2]/div/div[2]/div[2]/div/div/div[2]/div/div[1]/div[2]/div[3]");
    private final By adultDecrementButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[4]/form/div[2]/div/div[2]/div[2]/div/div/div[2]/div/div[2]/div[2]/div[3]");
    private final By childIncrementButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[4]/form/div[2]/div/div[2]/div[2]/div/div/div[2]/div/div[3]/div[2]/div[3]");
    private final By infantIncrementButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[4]/form/div[2]/div/div[2]/div[2]/div/div/div[2]/div/div[4]/div[2]/div[3]");
    private final By closePassengerDropdownButton = By.xpath("//button[contains(text(), 'Done')] | //button[contains(text(), 'Apply')]");

    // Promo Section
    private final By bundlePromoSection = By.xpath("//div[contains(text(), '$876 OFF')]");
    private final By bundleAndSaveButton = By.xpath("//button[contains(text(), 'Bundle')]");

    // Sign In Modal
    private final By signInModal = By.cssSelector("[role='dialog']");
    private final By emailSignInButton = By.xpath("//*[@id=\"page-initial\"]/div[4]/ul/li[1]");
    private final By googleSignInButton = By.xpath("//*[@id=\"login-modal-google\"]");
    private final By appleSignInButton = By.xpath("//*[@id=\"login-modal-apple\"]");
    private final By closeSignInModalButton = By.cssSelector("//*[@id=\"top-right-modal-close\"]");
    private final By signInModalTitle = By.xpath("//div[contains(text(), 'Sign in or register')]");

    // Email input form (after clicking Email)
    private final By emailInput = By.xpath("//*[@id=\"login-modal-account-login-email\"]");
    private final By continueButton = By.xpath("//button[contains(text(), 'Continue')]");
    private final By backButton = By.cssSelector("button[class*='back']");

    // Validation error messages
    private final By originValidationError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'origin') or contains(text(), 'leaving') or contains(text(), 'departure city'))]");
    private final By destinationValidationError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'destination') or contains(text(), 'going') or contains(text(), 'arrival city'))]");
    private final By departureDateValidationError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'departure') or contains(text(), 'departing') or contains(text(), 'date'))]");
    private final By returnDateValidationError = By.xpath("//*[contains(@class, 'error') and contains(text(), 'return')]");
    private final By dateValidationError = By.xpath("//*[contains(@class, 'error') and contains(text(), 'date')]");
    private final By passengerValidationError = By.xpath("//*[contains(@class, 'error') and contains(text(), 'passenger')]");
    private final By sameAirportError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'same') or contains(text(), 'different'))]");
    private final By invalidDateRangeError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'before') or contains(text(), 'after') or contains(text(), 'range'))]");
    private final By pastDateError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'past') or contains(text(), 'before today'))]");
    private final By invalidDateSequenceError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'sequence') or contains(text(), 'order') or contains(text(), 'before'))]");
    private final By multiCityValidationError = By.xpath("//*[contains(@class, 'error') and (contains(text(), 'flight') or contains(text(), 'segment'))]");

    // ========== CONSTRUCTOR ==========

    public HomePage() {
        logger.info("HomePage object initialized");
    }

    // ========== NAVIGATION ACTIONS ==========

    public HomePage navigateToHomePage() {
        DriverFactory.navigateToBaseUrl();
        // REMOVE: ElementActions.waitForPageLoad();
        // Just wait for the search button to prove the page is usable
        ElementActions.waitForVisibility(searchButton, 15);
        logger.info("Navigated to FlightHub homepage");
        return this;
    }

    public HomePage acceptCookies() {
        try {
            if (ElementActions.isDisplayed(acceptAllCookiesButton, 3)) {
                ElementActions.click(acceptAllCookiesButton);
                // Just give the banner a moment to fade — don't use waitForInvisibility
                // because it has a long default timeout (often 30s) and slows every test
                ElementActions.sleep(500);
                logger.info("Cookies accepted");
            }
        } catch (Exception e) {
            logger.debug("Cookie banner not present or already handled");
        }
        return this;
    }

    public HomePage rejectNonEssentialCookies() {
        try {
            if (ElementActions.isDisplayed(rejectNonEssentialCookiesButton, 3)) {
                ElementActions.click(rejectNonEssentialCookiesButton);
                logger.info("Non-essential cookies rejected");
            }
        } catch (Exception e) {
            logger.debug("Cookie banner not present");
        }
        return this;
    }

    // ========== SIGN IN / REGISTER (YOUR EXISTING CODE - UNCHANGED) ==========

    public HomePage clickSignInButton() {
        ElementActions.click(signInButton);
        ElementActions.waitForVisibility(signInModalTitle);
        logger.info("Sign in modal opened");
        return this;
    }

    public HomePage selectEmailSignIn() {
        ElementActions.click(emailSignInButton);
        ElementActions.waitForVisibility(emailInput);
        logger.info("Selected Email sign-in option");
        return this;
    }

    public HomePage enterEmail(String email) {
        ElementActions.type(emailInput, email);
        logger.info("Entered email: {}", email);
        return this;
    }

    public HomePage clickContinue() {
        ElementActions.click(continueButton);
        logger.info("Clicked Continue button");
        return this;
    }

    public HomePage closeSignInModal() {
        try {
            ElementActions.click(closeSignInModalButton);
            ElementActions.waitForInvisibility(signInModal);
            logger.info("Sign in modal closed");
        } catch (Exception e) {
            logger.warn("Could not close sign-in modal: {}", e.getMessage());
        }
        return this;
    }

    public boolean isSignInModalDisplayed() {
        return ElementActions.isDisplayed(signInModalTitle, 5);
    }

    public String getSignInModalTitle() {
        return ElementActions.getText(signInModalTitle);
    }

    // ========== TAB SELECTION ==========

    public HomePage clickFlightsTab() {
        ElementActions.click(flightsTab);
        logger.info("Clicked Flights tab");
        return this;
    }

    public HomePage clickHotelsTab() {
        ElementActions.click(hotelsTab);
        logger.info("Clicked Hotels tab");
        return this;
    }

    // ========== TRIP TYPE SELECTION ==========

    public HomePage selectRoundTrip() {
        ElementActions.click(roundTripOption);
        logger.info("Selected Round Trip");
        return this;
    }

    public HomePage selectOneWay() {
        ElementActions.click(oneWayOption);
        ElementActions.sleep(300);
        logger.info("Selected One Way");
        return this;
    }

    public HomePage selectMultiCity() {
        ElementActions.click(multiCityOption);
        ElementActions.sleep(500);
        logger.info("Selected Multi-City");
        return this;
    }

    // ========== LOCATION INPUT ==========

    public HomePage enterOrigin(String origin) {
        ElementActions.click(leavingFromField);
        ElementActions.type(leavingFromInput, origin);
        selectFirstAutocompleteOption();
        return this;
    }

    public HomePage enterDestination(String destination) {
        ElementActions.click(goingToField);
        ElementActions.type(goingToInput, destination);
        selectFirstAutocompleteOption();
        return this;
    }

    public HomePage clearOriginField() {
        try {
            WebElement input = DriverFactory.getDriver().findElement(leavingFromInput);
            JavascriptExecutor js = (JavascriptExecutor) DriverFactory.getDriver();

            // Clear value
            js.executeScript("arguments[0].value = '';", input);

            // Trigger React update
            js.executeScript(
                    "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));" +
                            "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                    input
            );

            ElementActions.sleep(300);
            logger.info("Cleared origin field via JavaScript");
        } catch (Exception e) {
            logger.warn("Could not clear origin field: {}", e.getMessage());
        }
        return this;
    }

    public HomePage clearDestinationField() {
        try {
            ElementActions.click(goingToField);
            ElementActions.sleep(200);
            ElementActions.clear(goingToInput);
            By clearButton = By.xpath("//input[contains(@id, 'seg0_to')]/following-sibling::*//button[contains(@class, 'close')] | //label[contains(text(), 'Going')]/following-sibling::div//button[contains(@class, 'clear')]");
            if (ElementActions.isDisplayed(clearButton, 2)) {
                ElementActions.click(clearButton);
            }
        } catch (Exception e) {
            logger.debug("Could not clear destination field");
        }
        logger.info("Cleared destination field");
        return this;
    }

    // ========== DATE SELECTION ==========

    public HomePage selectDepartureDateDaysFromToday(int daysFromToday) {
        ElementActions.click(departingDateField);
        ElementActions.sleep(200);   // was 500
        selectDateInCalendar(daysFromToday);
        logger.info("Selected departure date {} days from today", daysFromToday);
        return this;
    }

    public HomePage selectReturnDateDaysFromToday(int daysFromToday) {
        ElementActions.click(returningDateField);
        ElementActions.sleep(200);   // was 500
        selectDateInCalendar(daysFromToday);
        logger.info("Selected return date {} days from today", daysFromToday);
        return this;
    }

    public HomePage selectOneWayDateDaysFromToday(int daysFromToday) {
        By dateField = By.xpath("//label[contains(text(), 'Date')]/following-sibling::div | //div[contains(@id, 'seg0_date')]");
        ElementActions.click(dateField);
        ElementActions.sleep(500);
        selectDateInCalendar(daysFromToday);
        logger.info("Selected one-way date {} days from today", daysFromToday);
        return this;
    }

    public HomePage clearDepartureDate() {
        try {
            By clearButton = By.xpath("//div[contains(@id, 'seg0_date')]//button[contains(@class, 'clear')] | //label[contains(text(), 'Departing')]/following-sibling::div//button[contains(@class, 'clear')]");
            if (ElementActions.isDisplayed(clearButton, 2)) {
                ElementActions.click(clearButton);
            }
        } catch (Exception e) {
            logger.debug("Could not clear departure date");
        }
        logger.info("Cleared departure date");
        return this;
    }

    public HomePage clearReturnDate() {
        try {
            By clearButton = By.xpath("//div[contains(@id, 'seg1_date')]//button[contains(@class, 'clear')] | //label[contains(text(), 'Returning')]/following-sibling::div//button[contains(@class, 'clear')]");
            if (ElementActions.isDisplayed(clearButton, 2)) {
                ElementActions.click(clearButton);
            }
        } catch (Exception e) {
            logger.debug("Could not clear return date");
        }
        logger.info("Cleared return date");
        return this;
    }

    public HomePage clearOneWayDate() {
        try {
            By clearButton = By.xpath("//div[contains(@id, 'seg0_date')]//button[contains(@class, 'clear')] | //label[contains(text(), 'Date')]/following-sibling::div//button[contains(@class, 'clear')]");
            if (ElementActions.isDisplayed(clearButton, 2)) {
                ElementActions.click(clearButton);
            }
        } catch (Exception e) {
            logger.debug("Could not clear one-way date");
        }
        logger.info("Cleared one-way date");
        return this;
    }

    /**
     * Helper: Select a date N days from today in the calendar picker
     */
    private void selectDateInCalendar(int daysFromToday) {
        try {
            List<WebElement> availableDays = ElementActions.findElements(datePickerDayCell);
            if (!availableDays.isEmpty()) {
                int dayIndex = Math.min(daysFromToday - 1, availableDays.size() - 1);
                dayIndex = Math.max(dayIndex, 0);

                if (daysFromToday > availableDays.size()) {
                    int monthsToClick = daysFromToday / 30;
                    for (int i = 0; i < monthsToClick; i++) {
                        if (ElementActions.isDisplayed(datePickerNextMonthButton, 2)) {
                            ElementActions.click(datePickerNextMonthButton);
                            ElementActions.sleep(300);
                        }
                    }
                    availableDays = ElementActions.findElements(datePickerDayCell);
                    dayIndex = Math.min(daysFromToday % 30, availableDays.size() - 1);
                    dayIndex = Math.max(dayIndex, 0);
                }

                if (dayIndex < availableDays.size()) {
                    ElementActions.click(availableDays.get(dayIndex));
                    ElementActions.sleep(300);
                }

                // Click "Set dates" if present (for round trip)
                try {
                    if (ElementActions.isDisplayed(datePickerSetDatesButton, 2)) {
                        ElementActions.click(datePickerSetDatesButton);
                        ElementActions.sleep(300);
                    }
                } catch (Exception e) {
                    // Set dates button not present, that's fine for one-way
                }
            }
        } catch (Exception e) {
            logger.warn("Could not select date in calendar: {}", e.getMessage());
        }
    }

    /**
     * Select past departure date (for negative testing)
     */
    public HomePage selectPastDepartureDate() {
        ElementActions.click(departingDateField);
        ElementActions.sleep(500);
        try {
            if (ElementActions.isDisplayed(datePickerPrevMonthButton, 2)) {
                ElementActions.click(datePickerPrevMonthButton);
                ElementActions.sleep(300);
            }
            List<WebElement> days = ElementActions.findElements(datePickerDayCell);
            if (!days.isEmpty()) {
                ElementActions.click(days.get(days.size() - 1));
            }
        } catch (Exception e) {
            logger.warn("Could not select past date: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Select past one-way date (for negative testing)
     */
    public HomePage selectPastOneWayDate() {
        By dateField = By.xpath("//label[contains(text(), 'Date')]/following-sibling::div | //div[contains(@id, 'seg0_date')]");
        ElementActions.click(dateField);
        ElementActions.sleep(500);
        try {
            if (ElementActions.isDisplayed(datePickerPrevMonthButton, 2)) {
                ElementActions.click(datePickerPrevMonthButton);
                ElementActions.sleep(300);
            }
            List<WebElement> days = ElementActions.findElements(datePickerDayCell);
            if (!days.isEmpty()) {
                ElementActions.click(days.get(days.size() - 1));
            }
        } catch (Exception e) {
            logger.warn("Could not select past one-way date: {}", e.getMessage());
        }
        return this;
    }

    // ========== PASSENGER SELECTION ==========

    public HomePage selectPassengers(int adults, int children, int infants) {
        try {
            ElementActions.click(passengerDropdownTrigger);
            ElementActions.sleep(500);

            // Adjust adults from default (1)
            int currentAdults = 1;
            int adultDiff = adults - currentAdults;
            for (int i = 0; i < Math.abs(adultDiff); i++) {
                if (adultDiff > 0) {
                    ElementActions.click(adultIncrementButton);
                } else {
                    ElementActions.click(adultDecrementButton);
                }
                ElementActions.sleep(200);
            }

            // Set children
            for (int i = 0; i < children; i++) {
                ElementActions.click(childIncrementButton);
                ElementActions.sleep(200);
            }

            // Set infants
            for (int i = 0; i < infants; i++) {
                ElementActions.click(infantIncrementButton);
                ElementActions.sleep(200);
            }

            // Close dropdown
            try {
                if (ElementActions.isDisplayed(closePassengerDropdownButton, 2)) {
                    ElementActions.click(closePassengerDropdownButton);
                } else {
                    ElementActions.pressEscape();
                }
            } catch (Exception e) {
                ElementActions.pressEscape();
            }

            logger.info("Selected passengers: {} adults, {} children, {} infants", adults, children, infants);
        } catch (Exception e) {
            logger.warn("Could not select passengers: {}", e.getMessage());
        }
        return this;
    }

    // ========== MULTI-CITY METHODS ==========

    public HomePage clickAddCity() {
        ElementActions.click(addCityButton);
        ElementActions.sleep(500);
        logger.info("Clicked Add City button");
        return this;
    }

    public HomePage fillMultiCityLeg(int legNumber, String origin, String destination, int daysFromToday) {
        enterMultiCityOrigin(legNumber, origin);
        enterMultiCityDestination(legNumber, destination);
        selectMultiCityDate(legNumber, daysFromToday);
        logger.info("Filled multi-city leg {}: {} to {} ({} days)", legNumber, origin, destination, daysFromToday);
        return this;
    }

    public HomePage enterMultiCityOrigin(int legNumber, String origin) {
        try {
            By originInput = By.xpath("//input[@id='seg" + (legNumber - 1) + "_from_display'] | //div[contains(@id, 'seg" + (legNumber - 1) + "_from')]//input");
            By originField = By.xpath("//div[contains(@id, 'seg" + (legNumber - 1) + "_from')] | //label[contains(@for, 'seg" + (legNumber - 1) + "_from')]/following-sibling::*");
            ElementActions.click(originField);
            ElementActions.sleep(200);
            ElementActions.type(originInput, origin);
            ElementActions.sleep(500);
            selectFirstAutocompleteOption();
        } catch (Exception e) {
            logger.warn("Could not enter multi-city origin for leg {}: {}", legNumber, e.getMessage());
        }
        return this;
    }

    public HomePage enterMultiCityDestination(int legNumber, String destination) {
        try {
            By destInput = By.xpath("//input[@id='seg" + (legNumber - 1) + "_to_display'] | //div[contains(@id, 'seg" + (legNumber - 1) + "_to')]//input");
            By destField = By.xpath("//div[contains(@id, 'seg" + (legNumber - 1) + "_to')] | //label[contains(@for, 'seg" + (legNumber - 1) + "_to')]/following-sibling::*");
            ElementActions.click(destField);
            ElementActions.sleep(200);
            ElementActions.type(destInput, destination);
            ElementActions.sleep(500);
            selectFirstAutocompleteOption();
        } catch (Exception e) {
            logger.warn("Could not enter multi-city destination for leg {}: {}", legNumber, e.getMessage());
        }
        return this;
    }

    public HomePage selectMultiCityDate(int legNumber, int daysFromToday) {
        try {
            By dateField = By.xpath("//div[@id='seg" + (legNumber - 1) + "_date'] | //label[contains(@for, 'seg" + (legNumber - 1) + "_date')]/following-sibling::*");
            ElementActions.click(dateField);
            ElementActions.sleep(500);
            selectDateInCalendar(daysFromToday);
        } catch (Exception e) {
            logger.warn("Could not select multi-city date for leg {}: {}", legNumber, e.getMessage());
        }
        return this;
    }

    public HomePage clearMultiCityOrigin(int legNumber) {
        try {
            By clearBtn = By.xpath("//div[contains(@id, 'seg" + (legNumber - 1) + "_from')]//button[contains(@class, 'clear') or contains(@class, 'close')]");
            if (ElementActions.isDisplayed(clearBtn, 2)) {
                ElementActions.click(clearBtn);
            }
        } catch (Exception e) {
            logger.debug("Could not clear multi-city origin for leg {}", legNumber);
        }
        return this;
    }

    public HomePage clearMultiCityDestination(int legNumber) {
        try {
            By clearBtn = By.xpath("//div[contains(@id, 'seg" + (legNumber - 1) + "_to')]//button[contains(@class, 'clear') or contains(@class, 'close')]");
            if (ElementActions.isDisplayed(clearBtn, 2)) {
                ElementActions.click(clearBtn);
            }
        } catch (Exception e) {
            logger.debug("Could not clear multi-city destination for leg {}", legNumber);
        }
        return this;
    }

    public HomePage clearMultiCityDate(int legNumber) {
        try {
            By clearBtn = By.xpath("//div[@id='seg" + (legNumber - 1) + "_date']//button[contains(@class, 'clear')]");
            if (ElementActions.isDisplayed(clearBtn, 2)) {
                ElementActions.click(clearBtn);
            }
        } catch (Exception e) {
            logger.debug("Could not clear multi-city date for leg {}", legNumber);
        }
        return this;
    }

    public HomePage removeMultiCityLeg(int legNumber) {
        try {
            By removeBtn = By.xpath("//div[contains(@id, 'seg" + (legNumber - 1) + "')]//button[contains(@class, 'remove') or contains(@class, 'delete')]");
            if (ElementActions.isDisplayed(removeBtn, 2)) {
                ElementActions.click(removeBtn);
                ElementActions.sleep(500);
                logger.info("Removed multi-city leg {}", legNumber);
            }
        } catch (Exception e) {
            logger.warn("Could not remove multi-city leg {}", legNumber);
        }
        return this;
    }

    public boolean isAddCityButtonDisplayed() {
        return ElementActions.isDisplayed(addCityButton, 3);
    }

    public int getMultiCityLegCount() {
        try {
            List<WebElement> legs = DriverFactory.getDriver().findElements(
                    By.xpath("//div[contains(@id, 'seg') and contains(@id, '_from')]")
            );
            int count = legs.size();
            logger.info("Multi-city leg count: {}", count);
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    // ========== SEARCH ACTIONS ==========

    public FlightSearchResultsPage clickSearch() {
        ElementActions.click(searchButton);
        logger.info("Clicked Search button");
        return new FlightSearchResultsPage();
    }

    public HomePage clickSearchExpectingError() {
        ElementActions.click(searchButton);
        ElementActions.sleep(600);   // was 1500
        logger.info("Clicked search button expecting validation error");
        return this;
    }

    public boolean isSearchButtonEnabled() {
        try {
            WebElement btn = DriverFactory.getDriver().findElement(searchButton);  // ✅ Fixed
            return btn.isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    // ========== COMPLETE SEARCH FLOWS ==========

    public FlightSearchResultsPage searchFlights(String origin, String destination) {
        logger.info("Searching flights from {} to {}", origin, destination);
        return enterOrigin(origin)
                .enterDestination(destination)
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearch();
    }

    public FlightSearchResultsPage searchOneWayFlight(String origin, String destination) {
        logger.info("Searching one-way flight from {} to {}", origin, destination);
        return selectOneWay()
                .enterOrigin(origin)
                .enterDestination(destination)
                .selectOneWayDateDaysFromToday(7)
                .clickSearch();
    }

    // ========== VALIDATION ERROR CHECKS ==========

    public boolean isOriginValidationErrorDisplayed() {
        return ElementActions.isDisplayed(originValidationError, 3);
    }

    public boolean isDestinationValidationErrorDisplayed() {
        return ElementActions.isDisplayed(destinationValidationError, 3);
    }

    public boolean isDepartureDateValidationErrorDisplayed() {
        return ElementActions.isDisplayed(departureDateValidationError, 3);
    }

    public boolean isReturnDateValidationErrorDisplayed() {
        return ElementActions.isDisplayed(returnDateValidationError, 3);
    }

    public boolean isDateValidationErrorDisplayed() {
        return ElementActions.isDisplayed(dateValidationError, 3);
    }

    public boolean isPassengerValidationErrorDisplayed() {
        return ElementActions.isDisplayed(passengerValidationError, 3);
    }

    public boolean isSameAirportErrorDisplayed() {
        return ElementActions.isDisplayed(sameAirportError, 3);
    }

    public boolean isInvalidDateRangeErrorDisplayed() {
        return ElementActions.isDisplayed(invalidDateRangeError, 3);
    }

    public boolean isPastDateErrorDisplayed() {
        return ElementActions.isDisplayed(pastDateError, 3);
    }

    public boolean isInvalidDateSequenceErrorDisplayed() {
        return ElementActions.isDisplayed(invalidDateSequenceError, 3);
    }

    public boolean isMultiCityValidationErrorDisplayed(int legNumber) {
        return ElementActions.isDisplayed(multiCityValidationError, 3);
    }

    public boolean isMultiCityDateValidationErrorDisplayed(int legNumber) {
        return ElementActions.isDisplayed(dateValidationError, 3);
    }

    private final By validationModal = By.xpath("//div[contains(text(), 'Oops!') or contains(text(), 'missing some crucial')]");
    private final By validationModalTitle = By.xpath("//div[contains(text(), 'Oops! Looks like we're missing some crucial information')]");
    private final By validationModalCloseButton = By.xpath("//div[contains(text(), 'Oops!')]/following-sibling::button | //div[contains(text(), 'Oops!')]//button | //*[contains(@class, 'modal')]//button[contains(text(), '×') or contains(@class, 'close')]");
    /**
     * Check if the generic validation modal is displayed
     */
    public boolean isValidationModalDisplayed() {
        return ElementActions.isDisplayed(validationModal, 3);
    }

    /**
     * Get the validation modal text
     */
    public String getValidationModalText() {
        try {
            return ElementActions.getText(validationModal);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Close the validation modal if present
     */
    public HomePage closeValidationModal() {
        try {
            if (ElementActions.isDisplayed(validationModalCloseButton, 2)) {
                ElementActions.click(validationModalCloseButton);
                ElementActions.sleep(300);
                logger.info("Closed validation modal");
            }
        } catch (Exception e) {
            logger.debug("No validation modal to close");
        }
        return this;
    }

    public boolean isErrorDisplayed() {
        By genericError = By.xpath("//*[contains(@class, 'error') or contains(@class, 'alert')]");
        return ElementActions.isDisplayed(genericError, 3);
    }

    // ========== HELPER METHODS ==========

    private void selectFirstAutocompleteOption() {
        try {
            // Wait for dropdown container
            By dropdownContainer = By.cssSelector("div.airport-autocomplete-list");
            if (ElementActions.isDisplayed(dropdownContainer, 3)) {

                // Try multiple selectors for the first item
                // The items might be divs, not lis
                List<WebElement> options = ElementActions.findElements(
                        By.cssSelector("div.airport-autocomplete-list > div, " +
                                "div.airport-autocomplete-list li, " +
                                "div.airport-autocomplete-list__group > div, " +
                                "[class*='airport-autocomplete-list'] [class*='item']")
                );

                if (!options.isEmpty()) {
                    // Click the first option directly
                    options.get(0).click();
                    ElementActions.sleep(300);
                    logger.debug("Selected first autocomplete option: {}", options.get(0).getText());
                } else {
                    logger.warn("Dropdown appeared but no items found with standard selectors");
                }
            }
        } catch (Exception e) {
            logger.debug("No autocomplete option to select: {}", e.getMessage());
        }
    }

    // ========== VERIFICATION METHODS ==========

    public boolean isHomePageLoaded() {
        boolean logoDisplayed = ElementActions.isDisplayed(logo, 10);
        boolean searchButtonDisplayed = ElementActions.isDisplayed(searchButton, 10);
        boolean isLoaded = logoDisplayed && searchButtonDisplayed;
        logger.info("Home page loaded: {}", isLoaded);
        return isLoaded;
    }

    public String getPageTitle() {
        return DriverFactory.getPageTitle();
    }

    public boolean isFlightsTabSelected() {
        try {
            WebElement tab = DriverFactory.getDriver().findElement(flightsTab);
            String ariaSelected = tab.getDomAttribute("aria-selected");
            return "true".equals(ariaSelected) || tab.getDomAttribute("class").contains("active");
        } catch (Exception e) {
            return false;
        }
    }

    // ========== NAVIGATION LINKS ==========

    public HomePage clickSupportLink() {
        ElementActions.click(supportLink);
        logger.info("Clicked Support link");
        return this;
    }

    public HomePage clickMyTripsLink() {
        ElementActions.click(myTripsLink);
        logger.info("Clicked My Trips link");
        return this;
    }

    public HomePage scrollToBundlePromo() {
        ElementActions.scrollToElement(bundlePromoSection);
        logger.info("Scrolled to bundle promo section");
        return this;
    }

    public HomePage clickBundleAndSave() {
        ElementActions.click(bundleAndSaveButton);
        logger.info("Clicked Bundle & Save button");
        return this;
    }
}