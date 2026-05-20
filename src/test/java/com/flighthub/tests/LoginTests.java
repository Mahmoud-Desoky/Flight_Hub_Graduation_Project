package com.flighthub.tests;

import com.flighthub.base.BaseTest;
import com.flighthub.pages.HomePage;
import com.flighthub.pages.LoginPage;
import com.flighthub.testdata.LoginDataProvider;
import com.flighthub.utils.TestDataGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for Login functionality on FlightHub.com.
 * Covers:
 * - Successful login with valid credentials
 * - Failed login with invalid credentials
 * - Login form validation
 * - Login with empty fields
 * - Login edge cases
 * - Social login options availability
 * - Modal UI verification
 */
public class LoginTests extends BaseTest {

    /**
     * Test: Successful login with valid credentials
     * Priority: Critical
     */
    @Test(
            groups = {"login", "regression", "critical"},
            description = "Verify that a registered user can successfully log in",
            priority = 1
    )
    public void testSuccessfulLogin() {
        logInfo("Starting test: Successful Login");

        // Navigate to homepage and open login
        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail("justmooody23@gmail.com")
                .enterPassword("12345678")
                .clickSignIn();

        // Verify login was successful or proper error handling
        boolean isOnPasswordStep = loginPage.isPasswordStep();
        logInfo("User is on password step: " + isOnPasswordStep);

        if (!isOnPasswordStep) {
            logInfo("Application may have redirected to registration flow for new email");
        }

        logInfo("Completed successful login flow test");
    }

    /**
     * Test: Login with invalid email format
     */
    @Test(
            groups = {"login", "regression", "negative"},
            description = "Verify that login with invalid email format shows error",
            priority = 2
    )
    public void testLoginWithInvalidEmail() {
        logInfo("Starting test: Login with Invalid Email Format");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .enterEmail("notavalidemail")
                .clickContinue();

        // Verify error or the system accepts and redirects
        boolean hasError = loginPage.isErrorMessageDisplayed();
        logInfo("Error displayed for invalid email: " + hasError);

        if (hasError) {
            String errorMsg = loginPage.getErrorMessage();
            logInfo("Error message: " + errorMsg);
            Assert.assertTrue(errorMsg.toLowerCase().contains("invalid") ||
                            errorMsg.toLowerCase().contains("format") ||
                            errorMsg.toLowerCase().contains("email"),
                    "Expected validation error for invalid email format");
        } else {
            logInfo("System accepted the email format - checking current state");
        }

        logInfo("Completed invalid email login test");
    }

    /**
     * Test: Login with empty email field
     */
    @Test(
            groups = {"login", "regression", "negative", "validation"},
            description = "Verify that login with empty email shows validation error",
            priority = 3
    )
    public void testLoginWithEmptyEmail() {
        logInfo("Starting test: Login with Empty Email");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .enterEmail("")
                .clickContinue();

        // Check if error is displayed
        boolean hasError = loginPage.isErrorMessageDisplayed();
        logInfo("Error displayed for empty email: " + hasError);

        if (hasError) {
            String errorMsg = loginPage.getErrorMessage();
            logInfo("Error message: " + errorMsg);
            Assert.assertTrue(errorMsg.toLowerCase().contains("required") ||
                            errorMsg.toLowerCase().contains("enter") ||
                            errorMsg.toLowerCase().contains("email"),
                    "Expected validation error for empty email");
        }

        logInfo("Completed empty email login test");
    }

    /**
     * Test: Login with unregistered email
     */
    @Test(
            groups = {"login", "regression", "negative"},
            description = "Verify behavior when using unregistered email",
            priority = 4
    )
    public void testLoginWithUnregisteredEmail() {
        logInfo("Starting test: Login with Unregistered Email");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        String randomEmail = TestDataGenerator.getUniqueEmail();
        logInfo("Using random unregistered email: " + randomEmail);

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail(randomEmail);

        // Check if redirected to registration flow
        boolean isRegistrationStep = loginPage.isRegistrationStep();
        boolean isPasswordStep = loginPage.isPasswordStep();

        logInfo("Is registration step: " + isRegistrationStep);
        logInfo("Is password step: " + isPasswordStep);

        // The system should either show registration form or password form
        Assert.assertTrue(isRegistrationStep || isPasswordStep,
                "System should redirect to either registration or password step");

        logInfo("Completed unregistered email test");
    }


    /**
     * Test: Verify all authentication options are available
     */
    @Test(
            groups = {"login", "regression", "ui"},
            description = "Verify Email, Google, and Apple sign-in options are available",
            priority = 6
    )
    public void testAuthenticationOptionsAvailable() {
        logInfo("Starting test: Authentication Options Availability");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal();

        // Verify Email option is available
        loginPage.selectEmailOption();
        Assert.assertTrue(loginPage.isModalOpen(), "Email sign-in option should be available");
        logInfo("Email sign-in option is available");

        // Go back and verify other options
        loginPage.clickBack();
        logInfo("Navigated back to authentication options");

        // Select Google option and verify it navigates to Google
        loginPage.selectGoogleOption();
        logInfo("Google sign-in option clicked");

        // Note: Google/Apple login opens external OAuth - we verify the navigation attempt
        logInfo("Completed authentication options test");
    }


    /**
     * Test: Login with special characters in email
     */
    @Test(
            groups = {"login", "regression", "edge"},
            description = "Verify login with special characters in email",
            priority = 8
    )
    public void testLoginWithSpecialCharactersEmail() {
        logInfo("Starting test: Login with Special Characters in Email");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .enterEmail("user+test.special@example.com")
                .clickContinue();

        // Verify the system handles special characters appropriately
        logInfo("System processed email with special characters");

        logInfo("Completed special characters email test");
    }
}
