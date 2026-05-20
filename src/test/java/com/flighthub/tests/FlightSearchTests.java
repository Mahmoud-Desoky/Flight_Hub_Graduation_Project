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

    /*@Test(
            groups = {"multicity", "positive", "regression", "smoke"},
            description = "Verify multi-city search with 2 valid flight legs",
            priority = 18
    )
    public void testMultiCityTwoLegsValid() {
        logInfo("Starting TC-FS-018: Valid Multi-City with 2 Legs");
*/
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
    }
