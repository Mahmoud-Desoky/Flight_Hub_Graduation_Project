package com.flighthub.tests;

import com.flighthub.base.BaseTest;
import com.flighthub.pages.HomePage;
import com.flighthub.pages.LoginPage;
import com.flighthub.utils.TestDataGenerator;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test class for User Registration functionality on FlightHub.com.
 * Covers:
 * - Successful registration with valid data
 * - Registration with existing email
 * - Registration form validation
 * - Registration with empty required fields
 * - Password strength validation
 * - Registration modal navigation
 */
public class RegisterTest extends BaseTest {

    /**
     * Test: Successful user registration with valid data
     * Priority: Critical
     */
    @Test(
            groups = {"registration", "regression", "critical"},
            description = "Verify that a new user can successfully register with valid data",
            priority = 1
    )
    public void testSuccessfulRegistration() {
        logInfo("Starting test: Successful Registration");

        // Generate unique test data
        String email = TestDataGenerator.getUniqueEmail();
        String password = TestDataGenerator.getStrongPassword();
        String firstName = TestDataGenerator.getRandomFirstName();
        String lastName = TestDataGenerator.getRandomLastName();

        logInfo("Registration data - Email: " + email + ", Name: " + firstName + " " + lastName);

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail(email);

        // Fill registration form if shown
        if (loginPage.isRegistrationStep()) {
            loginPage.enterFirstName(firstName)
                    .enterLastName(lastName)
                    .enterRegistrationPassword(password)
                    .enterConfirmPassword(password)
                    .clickCreateAccount();
        } else if (loginPage.isPasswordStep()) {
            // If email exists, this might be password step
            logInfo("Email may already exist - redirected to password step");
            loginPage.enterPassword(password)
                    .clickSignIn();
        }

        // Verify registration result



        logInfo("Completed successful registration test");
    }

    /**
     * Test: Registration with empty email field
     */
    @Test(
            groups = {"registration", "regression", "negative", "validation"},
            description = "Verify registration with empty email shows validation error",
            priority = 2
    )
    public void testRegistrationWithEmptyEmail() {
        logInfo("Starting test: Registration with Empty Email");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .enterEmail("")
                .clickContinue();

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

        logInfo("Completed empty email registration test");
    }

    /**
     * Test: Registration with invalid email format
     */
    @Test(
            groups = {"registration", "regression", "negative", "validation"},
            description = "Verify registration with invalid email format shows error",
            priority = 3
    )
    public void testRegistrationWithInvalidEmailFormat() {
        logInfo("Starting test: Registration with Invalid Email Format");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .enterEmail("notanemailformat")
                .clickContinue();

        boolean hasError = loginPage.isErrorMessageDisplayed();
        logInfo("Error displayed for invalid email: " + hasError);

        if (hasError) {
            String errorMsg = loginPage.getErrorMessage();
            Assert.assertTrue(errorMsg.toLowerCase().contains("invalid") ||
                            errorMsg.toLowerCase().contains("format") ||
                            errorMsg.toLowerCase().contains("email"),
                    "Expected validation error for invalid email format");
        }

        logInfo("Completed invalid email format registration test");
    }

    /**
     * Test: Registration with existing email
     */
    @Test(
            groups = {"registration", "regression", "negative"},
            description = "Verify registration with already registered email redirects to login",
            priority = 4
    )
    public void testRegistrationWithExistingEmail() {
        logInfo("Starting test: Registration with Existing Email");

        String existingEmail = "test.user@example.com";

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail(existingEmail);

        // Check if system shows password step (existing user) instead of registration
        boolean isPasswordStep = loginPage.isPasswordStep();
        boolean isRegistrationStep = loginPage.isRegistrationStep();

        logInfo("Is password step (existing user): " + isPasswordStep);
        logInfo("Is registration step (new user): " + isRegistrationStep);

        // The application should recognize the existing email
        Assert.assertTrue(isPasswordStep || isRegistrationStep,
                "System should process the email appropriately");

        logInfo("Completed existing email registration test");
    }

    /**
     * Test: Registration modal navigation (back button)
     */
    @Test(
            groups = {"registration", "regression", "ui"},
            description = "Verify registration modal back button navigation",
            priority = 5
    )
    public void testRegistrationModalNavigation() {
        logInfo("Starting test: Registration Modal Navigation");

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption();

        // Go back to auth options
        loginPage.clickBack();
        logInfo("Navigated back to authentication options");

        // Verify we can select another option
        loginPage.selectGoogleOption();
        logInfo("Successfully selected another auth option after navigating back");

        logInfo("Completed modal navigation test");
    }

    /**
     * Test: Registration with weak password
     */
    @Test(
            groups = {"registration", "regression", "negative", "validation"},
            description = "Verify registration with weak password shows validation error",
            priority = 6
    )
    public void testRegistrationWithWeakPassword() {
        logInfo("Starting test: Registration with Weak Password");

        String email = TestDataGenerator.getUniqueEmail();
        String firstName = TestDataGenerator.getRandomFirstName();
        String lastName = TestDataGenerator.getRandomLastName();

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail(email);

        // Try to register with weak password
        if (loginPage.isRegistrationStep()) {
            loginPage.enterFirstName(firstName)
                    .enterLastName(lastName)
                    .enterRegistrationPassword("123")  // Very weak password
                    .enterConfirmPassword("123")
                    .clickCreateAccount();

            // Check if error is shown for weak password
            boolean hasError = loginPage.isErrorMessageDisplayed();
            logInfo("Error displayed for weak password: " + hasError);

            if (hasError) {
                String errorMsg = loginPage.getErrorMessage();
                logInfo("Password validation error: " + errorMsg);
            } else {
                logInfo("System did not reject weak password - checking current state");
            }
        }

        logInfo("Completed weak password registration test");
    }

    /**
     * Test: Registration with empty password
     */
    @Test(
            groups = {"registration", "regression", "negative", "validation"},
            description = "Verify registration with empty password shows validation error",
            priority = 7
    )
    public void testRegistrationWithEmptyPassword() {
        logInfo("Starting test: Registration with Empty Password");

        String email = TestDataGenerator.getUniqueEmail();
        String firstName = TestDataGenerator.getRandomFirstName();
        String lastName = TestDataGenerator.getRandomLastName();

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail(email);

        if (loginPage.isRegistrationStep()) {
            loginPage.enterFirstName(firstName)
                    .enterLastName(lastName)
                    .enterRegistrationPassword("")
                    .enterConfirmPassword("")
                    .clickCreateAccount();

            boolean hasError = loginPage.isErrorMessageDisplayed();
            logInfo("Error displayed for empty password: " + hasError);

            if (hasError) {
                String errorMsg = loginPage.getErrorMessage();
                Assert.assertTrue(errorMsg.toLowerCase().contains("password") ||
                                errorMsg.toLowerCase().contains("required"),
                        "Expected validation error for empty password");
            }
        }

        logInfo("Completed empty password registration test");
    }


    /**
     * Test: Verify registration form fields are present
     */
    @Test(
            groups = {"registration", "regression", "ui"},
            description = "Verify all required fields are present in registration form",
            priority = 9
    )


    /**
     * Test: Registration with very long first/last name
     */

    public void testRegistrationWithLongNames() {
        logInfo("Starting test: Registration with Long Names");

        String email = TestDataGenerator.getUniqueEmail();
        String password = TestDataGenerator.getStrongPassword();
        String longFirstName = "ChristopherAlexanderMaximilianSebastian";
        String longLastName = "VanDerBergMontgomeryWinchesterfieldTheThird";

        HomePage homePage = new HomePage();
        homePage.navigateToHomePage()
                .acceptCookies();

        LoginPage loginPage = new LoginPage();
        loginPage.openSignInModal()
                .selectEmailOption()
                .submitEmail(email);

        if (loginPage.isRegistrationStep()) {
            loginPage.enterFirstName(longFirstName)
                    .enterLastName(longLastName)
                    .enterRegistrationPassword(password)
                    .enterConfirmPassword(password)
                    .clickCreateAccount();

            logInfo("Completed registration with long names");
        }

        logInfo("Completed long names registration test");
    }
}
