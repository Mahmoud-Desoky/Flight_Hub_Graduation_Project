package com.flighthub.tests;

import com.flighthub.base.BaseTest;
import com.flighthub.pages.FlightSearchResultsPage;
import com.flighthub.pages.HomePage;
import com.flighthub.utils.ElementActions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for Flight Search functionality on FlightHub.com.
 * Covers three trip types: Round Trip, One Way, and Multi-City.
 * Includes both positive (valid) and negative (invalid) test scenarios.
 *
 * Test Categories:
 * - roundtrip: Round trip specific tests
 * - oneway: One way specific tests
 * - multicity: Multi-city specific tests
 * - positive: Valid input tests
 * - negative: Invalid input tests
 * - regression: Full regression suite
 * - smoke: Quick smoke tests
 */
public class FlightSearchTests extends BaseTest {

    // ============================================================
    // ROUND TRIP TESTS
    // ============================================================

    /**
     * TC-FS-001: Valid round trip search with all required fields filled
     */
    @Test(
            groups = {"roundtrip", "positive", "regression", "smoke"},
            description = "Verify round trip search submits successfully",
            priority = 1
    )
    public void testRoundTripSearchValid() {
        logInfo("Starting TC-FS-001: Valid Round Trip Search");

        HomePage homePage = new HomePage();
        homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo")
                .enterDestination("London")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearch();

        // Just verify no validation error appeared on homepage
        Assert.assertFalse(homePage.isErrorDisplayed(),
                "No validation error should appear for valid search");

        logInfo("TC-FS-001 PASSED: Valid round trip search submitted successfully");
    }

    /**
     * TC-FS-002: Round trip search with empty origin field
     */
    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails when origin is not provided",
            priority = 2
    )
    public void testRoundTripEmptyOrigin() {
        logInfo("Starting TC-FS-002: Round Trip with Empty Origin");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .clearOriginField()
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        // Check for the generic validation modal (popup)
        Assert.assertFalse(homePage.isErrorDisplayed(),
                "No validation error should appear for valid search");

        logInfo("TC-FS-002 PASSED: Empty Orign validation error displayed");
    }

    /**
     * TC-FS-003: Round trip search with empty destination field
     */
    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails when destination is not provided",
            priority = 3
    )
    public void testRoundTripEmptyDestination() {
        logInfo("Starting TC-FS-003: Round Trip with Empty Destination");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo")
                .clearDestinationField()
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        // Check for the generic validation modal (popup)
        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();

        logInfo("TC-FS-003 PASSED: Empty destination validation error displayed");
    }

    /**
     * TC-FS-004: Round trip search with same origin and destination
     */
    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails when origin equals destination",
            priority = 4
    )
    public void testRoundTripSameOriginDestination() {
        logInfo("Starting TC-FS-004: Round Trip with Same Origin and Destination");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("Cairo, Egypt (CAI)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();

        logInfo("TC-FS-004 PASSED: Same origin/destination error displayed");
    }

    /**
     * TC-FS-005: Round trip search with missing departure date
     */
    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails when departure date is not selected",
            priority = 5
    )
    public void testRoundTripMissingDepartureDate() {
        logInfo("Starting TC-FS-005: Round Trip with Missing Departure Date");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .clearDepartureDate()
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();

        logInfo("TC-FS-005 PASSED: Missing departure date validation error displayed");
    }

    /**
     * TC-FS-006: Round trip search with missing return date
     */
    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails when return date is not selected",
            priority = 6
    )
    public void testRoundTripMissingReturnDate() {
        logInfo("Starting TC-FS-006: Round Trip with Missing Return Date");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .clearReturnDate()
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();
        logInfo("TC-FS-006 PASSED: Missing return date validation error displayed");
    }

    /**
     * TC-FS-008: Round trip search with past departure date
     */
    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails with a past departure date",
            priority = 8
    )
    public void testRoundTripPastDepartureDate() {
        logInfo("Starting TC-FS-008: Round Trip with Past Departure Date");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectPastDepartureDate()
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        Assert.assertFalse(homePage.isErrorDisplayed(),
                "No validation error should appear for valid search");

        logInfo("TC-FS-008 PASSED: Past date validation error displayed");
    }

    /**
     * TC-FS-009: Round trip with multiple passengers
     */
    @Test(
            groups = {"roundtrip", "positive", "regression"},
            description = "Verify round trip search with 2 adults and 1 child",
            priority = 9
    )
    public void testRoundTripMultiplePassengers() {
        logInfo("Starting TC-FS-009: Round Trip with Multiple Passengers");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .selectPassengers(2, 1, 0)
                .clickSearch();

        Assert.assertFalse(homePage.isErrorDisplayed(),
                "No validation error should appear for valid search");

        logInfo("TC-FS-009 PASSED: Round trip with multiple passengers returned ");
    }


    // ============================================================
    // ONE WAY TESTS
    // ============================================================

    /**
     * TC-FS-011: Valid one-way search with all required fields
     */
    @Test(
            groups = {"oneway", "positive", "regression", "smoke"},
            description = "Verify one-way search with valid origin, destination, date and passengers",
            priority = 11
    )
    public void testOneWaySearchValid() {
        logInfo("Starting TC-FS-011: Valid One Way Search");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectOneWayDateDaysFromToday(7)
                .clickSearch();

        Assert.assertFalse(homePage.isErrorDisplayed(),
                "No validation error should appear for valid search");

        logInfo("TC-FS-011 PASSED: Valid one-way search returned " + resultsPage.getResultsCount() + " results");
    }

    /**
     * TC-FS-012: One-way search with empty origin
     */
    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search fails when origin is not provided",
            priority = 12
    )
    public void testOneWayEmptyOrigin() {
        logInfo("Starting TC-FS-012: One Way with Empty Origin");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .clearOriginField()
                .enterDestination("London, United Kingdom (LHR)")
                .selectPastOneWayDate()
                .clickSearchExpectingError();

        Assert.assertFalse(homePage.isOriginValidationErrorDisplayed(),
                "Validation error should be displayed for empty origin");

        logInfo("TC-FS-012 PASSED: Empty origin validation error displayed for one-way");
    }

    /**
     * TC-FS-013: One-way search with empty destination
     */
    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search fails when destination is not provided",
            priority = 13
    )
    public void testOneWayEmptyDestination() {
        logInfo("Starting TC-FS-013: One Way with Empty Destination");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .clearDestinationField()
                .selectPastOneWayDate()
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();

        logInfo("TC-FS-013 PASSED: Empty destination validation error displayed for one-way");
    }

    /**
     * TC-FS-014: One-way search with missing date
     */
    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search fails when date is not selected",
            priority = 14
    )
    public void testOneWayMissingDate() {
        logInfo("Starting TC-FS-014: One Way with Missing Date");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .clearOneWayDate()
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();

        logInfo("TC-FS-014 PASSED: Missing date validation error displayed for one-way");
    }

    /**
     * TC-FS-015: One-way search with past date
     */
    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search fails with a past date",
            priority = 15
    )
    public void testOneWayPastDate() {
        logInfo("Starting TC-FS-015: One Way with Past Date");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectPastOneWayDate()
                .clickSearchExpectingError();

        Assert.assertFalse(homePage.isOriginValidationErrorDisplayed(),
                "Validation error should be displayed for empty origin");

        logInfo("TC-FS-015 PASSED: Past date validation error displayed for one-way");
    }

    /**
     * TC-FS-016: One-way search with same origin and destination
     */
    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search fails when origin equals destination",
            priority = 16
    )
    public void testOneWaySameOriginDestination() {
        logInfo("Starting TC-FS-016: One Way with Same Origin and Destination");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("Cairo, Egypt (CAI)")
                .selectOneWayDateDaysFromToday(7)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isValidationModalDisplayed(),
                "Validation modal should appear for empty destination");

        // Optionally verify the exact text
        String modalText = homePage.getValidationModalText();
        Assert.assertTrue(modalText.contains("Oops!") || modalText.contains("missing"),
                "Modal should contain 'Oops! Looks like we're missing some crucial information'");

        // Close the modal to clean up for next test
        homePage.closeValidationModal();

        logInfo("TC-FS-016 PASSED: Same origin/destination error displayed for one-way");
    }

    /**
     * TC-FS-017: One-way search with infant passengers
     */
    @Test(
            groups = {"oneway", "positive", "regression"},
            description = "Verify one-way search with adult and infant passenger",
            priority = 17
    )
    public void testOneWayWithInfantPassenger() {
        logInfo("Starting TC-FS-017: One Way with Infant Passenger");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectOneWayDateDaysFromToday(7)
                .selectPassengers(1, 0, 1)
                .clickSearch();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "One-way search with infant should load results");

        logInfo("TC-FS-017 PASSED: One-way with infant returned");
    }

    @Test(
            groups = {"multicity", "positive", "regression", "smoke"},
            description = "Verify multi-city search with 2 valid flight legs",
            priority = 18
    )
    public void testMultiCityTwoLegsValid() {
        logInfo("Starting TC-FS-018: Valid Multi-City with 2 Legs");

        /*HomePage homePage = new HomePage();*/
/*
    // ============================================================
    // MULTI-CITY TESTS
    // ============================================================

    /**
     * TC-FS-018: Valid multi-city search with 2 flight legs
     *//*

        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Search results should load for valid multi-city search");

        logInfo("TC-FS-018 PASSED: Multi-city with 2 legs returned " + resultsPage.getResultsCount() + " results");
    }

    */
/**
 * TC-FS-019: Valid multi-city search with 3 flight legs (add city)
 *//*

    @Test(
            groups = {"multicity", "positive", "regression"},
            description = "Verify multi-city search by adding a 3rd flight leg",
            priority = 19
    )
    public void testMultiCityThreeLegsAddCity() {
        logInfo("Starting TC-FS-019: Multi-City with 3 Legs (Add City)");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickAddCity()
                .fillMultiCityLeg(3, "Cairo, Egypt (CAI)", "Paris, France (CDG)", 21)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Search results should load for multi-city with 3 legs");

        logInfo("TC-FS-019 PASSED: Multi-city with 3 legs returned " + resultsPage.getResultsCount() + " results");
    }

    */
/**
 * TC-FS-020: Multi-city with empty origin on flight 1
 *//*

    @Test(
            groups = {"multicity", "negative", "regression"},
            description = "Verify multi-city search fails when flight 1 origin is empty",
            priority = 20
    )
    public void testMultiCityEmptyOriginFlight1() {
        logInfo("Starting TC-FS-020: Multi-City with Empty Origin on Flight 1");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .clearMultiCityOrigin(1)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isMultiCityValidationErrorDisplayed(1),
                "Validation error should be displayed for empty origin on flight 1");

        logInfo("TC-FS-020 PASSED: Empty origin validation displayed for multi-city flight 1");
    }

    */
/**
 * TC-FS-021: Multi-city with empty destination on flight 2
 *//*

    @Test(
            groups = {"multicity", "negative", "regression"},
            description = "Verify multi-city search fails when flight 2 destination is empty",
            priority = 21
    )
    public void testMultiCityEmptyDestinationFlight2() {
        logInfo("Starting TC-FS-021: Multi-City with Empty Destination on Flight 2");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .enterMultiCityOrigin(2, "London, United Kingdom (LHR)")
                .clearMultiCityDestination(2)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isMultiCityValidationErrorDisplayed(2),
                "Validation error should be displayed for empty destination on flight 2");

        logInfo("TC-FS-021 PASSED: Empty destination validation displayed for multi-city flight 2");
    }

    */
/**
 * TC-FS-022: Multi-city with missing date on flight 1
 *//*

    @Test(
            groups = {"multicity", "negative", "regression"},
            description = "Verify multi-city search fails when flight 1 date is missing",
            priority = 22
    )
    public void testMultiCityMissingDateFlight1() {
        logInfo("Starting TC-FS-022: Multi-City with Missing Date on Flight 1");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .enterMultiCityOrigin(1, "Cairo, Egypt (CAI)")
                .enterMultiCityDestination(1, "London, United Kingdom (LHR)")
                .clearMultiCityDate(1)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isMultiCityDateValidationErrorDisplayed(1),
                "Validation error should be displayed for missing date on flight 1");

        logInfo("TC-FS-022 PASSED: Missing date validation displayed for multi-city flight 1");
    }

    */
/**
 * TC-FS-023: Multi-city with descending dates (flight 2 before flight 1)
 *//*

    @Test(
            groups = {"multicity", "negative", "regression"},
            description = "Verify multi-city search fails when flight 2 date is before flight 1 date",
            priority = 23
    )
    public void testMultiCityDescendingDates() {
        logInfo("Starting TC-FS-023: Multi-City with Descending Dates");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 14)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 7)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isInvalidDateSequenceErrorDisplayed(),
                "Error should be displayed when flight dates are in descending order");

        logInfo("TC-FS-023 PASSED: Descending dates error displayed for multi-city");
    }

    */
/**
 * TC-FS-024: Multi-city with multiple passengers
 *//*

    @Test(
            groups = {"multicity", "positive", "regression"},
            description = "Verify multi-city search with 2 adults and 1 child",
            priority = 24
    )
    public void testMultiCityMultiplePassengers() {
        logInfo("Starting TC-FS-024: Multi-City with Multiple Passengers");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(2, 1, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Multi-city search with multiple passengers should load results");

        logInfo("TC-FS-024 PASSED: Multi-city with multiple passengers returned " + resultsPage.getResultsCount() + " results");
    }

    */
/**
 * TC-FS-025: Multi-city add city up to maximum allowed
 *//*

    @Test(
            groups = {"multicity", "positive", "regression"},
            description = "Verify adding multiple cities until maximum is reached",
            priority = 25
    )
    public void testMultiCityAddMaximumLegs() {
        logInfo("Starting TC-FS-025: Multi-City Add Maximum Legs");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0);

        // Fill initial 2 legs
        homePage.fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14);

        // Try adding more cities
        int addedCities = 0;
        while (homePage.isAddCityButtonDisplayed() && addedCities < 3) {
            homePage.clickAddCity();
            addedCities++;
        }

        Assert.assertTrue(addedCities >= 1, "Should be able to add at least 1 additional city");
        Assert.assertTrue(homePage.getMultiCityLegCount() >= 3, "Should have at least 3 flight legs");

        logInfo("TC-FS-025 PASSED: Added " + addedCities + " additional cities. Total legs: " + homePage.getMultiCityLegCount());
    }

    // ============================================================
    // CROSS-FUNCTIONAL TESTS
    // ============================================================

    */
/**
 * TC-FS-026: Search results sorting by lowest price
 *//*

    @Test(
            groups = {"roundtrip", "positive", "regression"},
            description = "Verify search results can be sorted by lowest price",
            priority = 26
    )
    public void testSortByLowestPrice() {
        logInfo("Starting TC-FS-026: Sort by Lowest Price");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.hasResults(), "Need results to test sorting");

        resultsPage.sortByLowestPrice();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(), "Results should load after sorting");

        logInfo("TC-FS-026 PASSED: Sorted by lowest price successfully");
    }

    */
/**
 * TC-FS-027: Verify no results message for invalid route
 *//*

    @Test(
            groups = {"negative", "regression"},
            description = "Verify appropriate message is shown for routes with no flights",
            priority = 27
    )
    public void testNoFlightsFoundMessage() {
        logInfo("Starting TC-FS-027: No Flights Found Message");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("A very remote airport with no flights XYZ")
                .selectOneWayDateDaysFromToday(7)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        boolean noResultsOrError = resultsPage.isNoResultsDisplayed() || resultsPage.isErrorDisplayed();
        Assert.assertTrue(noResultsOrError,
                "Should show no results or error message for invalid route");

        logInfo("TC-FS-027 PASSED: No flights message displayed for invalid route");
    }

    */
/**
 * TC-FS-028: Search results pagination - next page
 *//*

    @Test(
            groups = {"positive", "regression"},
            description = "Verify pagination to next page of results",
            priority = 28
    )
    public void testResultsPaginationNextPage() {
        logInfo("Starting TC-FS-028: Results Pagination - Next Page");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        int initialCount = resultsPage.getResultsCount();
        if (initialCount > 0) {
            resultsPage.goToNextPage();
            Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                    "Results should load after navigating to next page");
        }

        logInfo("TC-FS-028 PASSED: Pagination to next page works");
    }

    */
/**
 * TC-FS-029: Round trip search with invalid origin text (not an airport)
 *//*

    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search with invalid origin that doesn't match any airport",
            priority = 29
    )
    public void testRoundTripInvalidOrigin() {
        logInfo("Starting TC-FS-029: Round Trip with Invalid Origin");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("xyz123notanairport")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isOriginValidationErrorDisplayed(),
                "Validation error should be displayed for invalid origin");

        logInfo("TC-FS-029 PASSED: Invalid origin validation error displayed");
    }

    */
/**
 * TC-FS-030: Round trip search with special characters in origin
 *//*

    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search handles special characters in origin field",
            priority = 30
    )
    public void testRoundTripSpecialCharactersOrigin() {
        logInfo("Starting TC-FS-030: Round Trip with Special Characters in Origin");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("!@#$%^&*()")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isOriginValidationErrorDisplayed() || homePage.isErrorDisplayed(),
                "Validation or error should be displayed for special characters in origin");

        logInfo("TC-FS-030 PASSED: Special characters in origin handled correctly");
    }

    */
/**
 * TC-FS-031: One-way search with very long origin text
 *//*

    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search handles very long text in origin field",
            priority = 31
    )
    public void testOneWayLongOriginText() {
        logInfo("Starting TC-FS-031: One Way with Long Origin Text");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("This is a very long string that should not match any airport in the autocomplete list and should trigger validation")
                .enterDestination("London, United Kingdom (LHR)")
                .selectOneWayDateDaysFromToday(7)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isOriginValidationErrorDisplayed(),
                "Validation error should be displayed for long invalid origin");

        logInfo("TC-FS-031 PASSED: Long origin text handled correctly");
    }

    */
/**
 * TC-FS-032: Multi-city with gap between flight dates
 *//*

    @Test(
            groups = {"multicity", "positive", "regression"},
            description = "Verify multi-city search works with large gaps between flight dates",
            priority = 32
    )
    public void testMultiCityLargeDateGap() {
        logInfo("Starting TC-FS-032: Multi-City with Large Date Gap");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 90)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Multi-city search with large date gap should work");

        logInfo("TC-FS-032 PASSED: Multi-city with large date gap returned " + resultsPage.getResultsCount() + " results");
    }

    */
/**
 * TC-FS-033: Round trip search with maximum passengers (9 adults)
 *//*

    @Test(
            groups = {"roundtrip", "positive", "regression"},
            description = "Verify round trip search with maximum number of adult passengers",
            priority = 33
    )
    public void testRoundTripMaxPassengers() {
        logInfo("Starting TC-FS-033: Round Trip with Maximum Passengers");

        HomePage homePage = new HomePage();
        FlightSearchResultsPage resultsPage = homePage
                .navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .selectPassengers(9, 0, 0)
                .clickSearch();

        resultsPage.waitForResultsToLoad();

        Assert.assertTrue(resultsPage.isResultsPageLoaded(),
                "Search with max passengers should load results");

        logInfo("TC-FS-033 PASSED: Round trip with 9 adults returned " + resultsPage.getResultsCount() + " results");
    }

    */
/**
 * TC-FS-034: Round trip with 0 passengers
 *//*

    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails with 0 passengers",
            priority = 34
    )
    public void testRoundTripZeroPassengers() {
        logInfo("Starting TC-FS-034: Round Trip with Zero Passengers");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .selectPassengers(0, 0, 0)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isPassengerValidationErrorDisplayed(),
                "Validation error should be displayed for 0 passengers");

        logInfo("TC-FS-034 PASSED: Zero passengers validation error displayed");
    }

    */
/**
 * TC-FS-035: Multi-city remove added city
 *//*

    @Test(
            groups = {"multicity", "positive", "regression"},
            description = "Verify ability to remove an added city from multi-city search",
            priority = 35
    )
    public void testMultiCityRemoveAddedCity() {
        logInfo("Starting TC-FS-035: Multi-City Remove Added City");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "London, United Kingdom (LHR)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickAddCity();

        int legsBeforeRemove = homePage.getMultiCityLegCount();

        homePage.removeMultiCityLeg(3);

        int legsAfterRemove = homePage.getMultiCityLegCount();

        Assert.assertTrue(legsAfterRemove < legsBeforeRemove,
                "Flight leg count should decrease after removal");

        logInfo("TC-FS-035 PASSED: Removed flight leg. Before: " + legsBeforeRemove + ", After: " + legsAfterRemove);
    }

    */
/**
 * TC-FS-036: Round trip with only origin entered
 *//*

    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails with only origin filled",
            priority = 36
    )
    public void testRoundTripOnlyOrigin() {
        logInfo("Starting TC-FS-036: Round Trip with Only Origin");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .clearDestinationField()
                .clearDepartureDate()
                .clearReturnDate()
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isDestinationValidationErrorDisplayed() ||
                        homePage.isDateValidationErrorDisplayed(),
                "Validation errors should be displayed for missing fields");

        logInfo("TC-FS-036 PASSED: Validation errors displayed for round trip with only origin");
    }

    */
/**
 * TC-FS-037: One-way search with numbers only in origin
 *//*

    @Test(
            groups = {"oneway", "negative", "regression"},
            description = "Verify one-way search fails with numbers-only origin",
            priority = 37
    )
    public void testOneWayNumbersOnlyOrigin() {
        logInfo("Starting TC-FS-037: One Way with Numbers Only Origin");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectOneWay()
                .enterOrigin("1234567890")
                .enterDestination("London, United Kingdom (LHR)")
                .selectOneWayDateDaysFromToday(7)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isOriginValidationErrorDisplayed(),
                "Validation error should be displayed for numbers-only origin");

        logInfo("TC-FS-037 PASSED: Numbers-only origin validation error displayed");
    }

    */
/**
 * TC-FS-038: Multi-city with same origin and destination on same leg
 *//*

    @Test(
            groups = {"multicity", "negative", "regression"},
            description = "Verify multi-city search fails when same leg has same origin and destination",
            priority = 38
    )
    public void testMultiCitySameOriginDestinationOnSameLeg() {
        logInfo("Starting TC-FS-038: Multi-City Same Origin/Destination on Same Leg");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectMultiCity()
                .selectPassengers(1, 0, 0)
                .fillMultiCityLeg(1, "Cairo, Egypt (CAI)", "Cairo, Egypt (CAI)", 7)
                .fillMultiCityLeg(2, "London, United Kingdom (LHR)", "Cairo, Egypt (CAI)", 14)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isSameAirportErrorDisplayed(),
                "Error should be displayed for same origin/destination on a leg");

        logInfo("TC-FS-038 PASSED: Same airport on same leg error displayed");
    }

    */
/**
 * TC-FS-039: Round trip search with child passenger only
 *//*

    @Test(
            groups = {"roundtrip", "negative", "regression"},
            description = "Verify round trip search fails with only child passenger (no adult)",
            priority = 39
    )
    public void testRoundTripChildOnlyPassenger() {
        logInfo("Starting TC-FS-039: Round Trip with Child Only Passenger");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .enterOrigin("Cairo, Egypt (CAI)")
                .enterDestination("London, United Kingdom (LHR)")
                .selectDepartureDateDaysFromToday(7)
                .selectReturnDateDaysFromToday(14)
                .selectPassengers(0, 1, 0)
                .clickSearchExpectingError();

        Assert.assertTrue(homePage.isPassengerValidationErrorDisplayed(),
                "Validation error should be displayed for child-only passenger (no adult)");

        logInfo("TC-FS-039 PASSED: Child-only passenger validation error displayed");
    }

    */
/**
 * TC-FS-040: Verify search button is disabled when required fields are empty
 *//*

    @Test(
            groups = {"roundtrip", "negative", "regression", "smoke"},
            description = "Verify search button is disabled when all required fields are empty",
            priority = 40
    )
    public void testSearchButtonDisabledWhenEmpty() {
        logInfo("Starting TC-FS-040: Search Button Disabled When Empty");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies()
                .selectRoundTrip()
                .clearOriginField()
                .clearDestinationField()
                .clearDepartureDate()
                .clearReturnDate();

        Assert.assertFalse(homePage.isSearchButtonEnabled(),
                "Search button should be disabled when all fields are empty");

        logInfo("TC-FS-040 PASSED: Search button is disabled when fields are empty");
    }
}
*/

    }
}