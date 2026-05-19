package com.flighthub.pages;

import com.flighthub.driver.DriverFactory;
import com.flighthub.utils.ElementActions;
import com.flighthub.utils.WaitUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;

/**
 * Page Object class for the FlightHub Login / Registration flow.
 * Handles the complete authentication flow including email entry,
 * password entry, registration form, and verification.
 */
public class LoginPage {

    private static final Logger logger = LogManager.getLogger(LoginPage.class);

    // ========== LOCATORS ==========

    // Sign In Modal - Initial View
    private final By signInModal = By.cssSelector("[role='dialog']");
    private final By signInModalTitle = By.xpath("//*[@id=\"home-top-section\"]/div/div[1]/div/div/div[2]/div[4]");
    private final By emailSignInButton = By.xpath("//*[@id=\"login-modal-email\"]");
    private final By googleSignInButton = By.xpath("//*[@id=\"login-modal-google\"]");
    private final By appleSignInButton = By.xpath("//button[.//label[contains(text(), 'Apple')]]");
    private final By closeModalButton = By.xpath("//*[@id=\"top-right-modal-close\"]/svg");
    private final By backArrowButton = By.xpath("//*[@id=\"page-email\"]/a");

    // Email Input Step
    private final By emailInput = By.xpath("//*[@id=\"login-modal-account-login-email\"]");
    private final By emailInputLabel = By.xpath("//*[@id=\"login-modal-account-login-email\"]");
    private final By continueButton = By.xpath("//button[contains(text(), 'Continue')]");
    private final By emailErrorMessage = By.xpath("//p[contains(@class, 'error')]");

    // Password Step (Login for existing users)
    private final By passwordInput = By.xpath("//*[@id=\"login-modal-account-login-password\"]");
    private final By passwordInputLabel = By.xpath("//*[@id=\"login-modal-account-login-password\"]");
    private final By signInSubmitButton = By.xpath("//*[@id=\"login-auth-form\"]/div/div/button");
    private final By loginErrorMessage = By.xpath("//div[contains(@class, 'error')]");

    // Registration Step (New users)
    private final By firstNameInput = By.cssSelector("input[name*='firstName' i], input[placeholder*='First' i]");
    private final By lastNameInput = By.cssSelector("input[name*='lastName' i], input[placeholder*='Last' i]");
    private final By registerPasswordInput = By.xpath("//*[@id=\"login-modal-account-register-password\"]");
    private final By confirmPasswordInput = By.xpath("//*[@id=\"login-modal-account-register-confirm-password\"]");
    private final By createAccountButton = By.xpath("//*[@id=\"login-registration-form\"]/ul/li[6]/button");


    // Account Dashboard (after successful login)
    private final By userAccountDropdown = By.xpath("//div[contains(@class, 'account')]");
    private final By logoutButton = By.xpath("//button[contains(text(), 'Log out') or contains(text(), 'Sign out')]");
    private final By welcomeMessage = By.xpath("//div[contains(text(), 'Welcome')]");
    private final By myAccountLink = By.linkText("My Account");

    // OTP/Verification
    private final By otpInput = By.cssSelector("input[type='number'], input[placeholder*='code' i]");
    private final By verifyButton = By.xpath("//button[contains(text(), 'Verify')]");
    private final By resendCodeLink = By.xpath("//button[contains(text(), 'Resend')]");

    // ========== CONSTRUCTOR ==========

    public LoginPage() {
        logger.info("LoginPage object initialized");
    }

    // ========== MODAL NAVIGATION ==========

    /**
     * Open the sign-in modal from homepage
     */
    public LoginPage openSignInModal() {
        By signInButton = By.xpath("//*[@id=\"home-top-section\"]/div/div[1]/div/div/div[2]/div[4]");
        ElementActions.click(signInButton);
        ElementActions.waitForVisibility(signInModalTitle);
        logger.info("Sign-in modal opened");
        return this;
    }

    /**
     * Select Email authentication method
     */
    public LoginPage selectEmailOption() {
        ElementActions.click(emailSignInButton);
        ElementActions.waitForVisibility(emailInput);
        logger.info("Selected Email authentication option");
        return this;
    }

    /**
     * Select Google authentication method
     */
    public LoginPage selectGoogleOption() {
        ElementActions.click(googleSignInButton);
        logger.info("Selected Google authentication option");
        return this;
    }

    /**
     * Select Apple authentication method
     */
    public LoginPage selectAppleOption() {
        ElementActions.click(appleSignInButton);
        logger.info("Selected Apple authentication option");
        return this;
    }

    /**
     * Close the sign-in modal
     */
    public HomePage closeModal() {
        try {
            ElementActions.click(closeModalButton);
            ElementActions.waitForInvisibility(signInModal);
            logger.info("Sign-in modal closed");
        } catch (Exception e) {
            logger.warn("Could not close modal: {}", e.getMessage());
        }
        return new HomePage();
    }

    /**
     * Go back to the previous step in authentication flow
     */
    public LoginPage clickBack() {
        try {
            ElementActions.click(backArrowButton);
            logger.info("Clicked back button");
        } catch (Exception e) {
            logger.warn("Back button not found or not clickable");
        }
        return this;
    }

    // ========== EMAIL INPUT STEP ==========

    /**
     * Enter email address
     */
    public LoginPage enterEmail(String email) {
        ElementActions.waitForVisibility(emailInput);
        ElementActions.type(emailInput, email);
        logger.info("Entered email address");
        return this;
    }

    /**
     * Click Continue after entering email
     */
    public LoginPage clickContinue() {
        ElementActions.click(continueButton);
        ElementActions.sleep(1000); // Wait for next step to load
        logger.info("Clicked Continue button");
        return this;
    }

    /**
     * Complete email step and proceed
     */
    public LoginPage submitEmail(String email) {
        return enterEmail(email).clickContinue();
    }

    // ========== LOGIN (EXISTING USER) ==========

    /**
     * Enter password for existing user login
     */
    public LoginPage enterPassword(String password) {
        ElementActions.waitForVisibility(passwordInput);
        ElementActions.type(passwordInput, password);
        logger.info("Entered password");
        return this;
    }

    /**
     * Enter confirm password for new account creation
     */
    public LoginPage enterConfirmPassword(String password) {
        try {
            ElementActions.waitForVisibility(confirmPasswordInput);
            ElementActions.type(confirmPasswordInput, password);
            logger.info("Entered confirm password");
        } catch (Exception e) {
            logger.warn("Confirm password input not found: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Click Sign in button for login
     */
    public LoginPage clickSignIn() {
        ElementActions.click(signInSubmitButton);
        ElementActions.sleep(2000); // Wait for login process
        logger.info("Clicked Sign in button");
        return this;
    }

    /**
     * Complete login flow for existing user
     */
    public LoginPage login(String email, String password) {
        logger.info("Performing login with email: {}", email);
        return openSignInModal()
                .selectEmailOption()
                .submitEmail(email)
                .enterPassword(password)
                .clickSignIn();
    }




    // ========== REGISTRATION (NEW USER) ==========

    /**
     * Enter first name for registration
     */
    public LoginPage enterFirstName(String firstName) {
        try {
            ElementActions.waitForVisibility(firstNameInput);
            ElementActions.type(firstNameInput, firstName);
            logger.info("Entered first name: {}", firstName);
        } catch (Exception e) {
            logger.warn("First name field not found: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Enter last name for registration
     */
    public LoginPage enterLastName(String lastName) {
        try {
            ElementActions.waitForVisibility(lastNameInput);
            ElementActions.type(lastNameInput, lastName);
            logger.info("Entered last name: {}", lastName);
        } catch (Exception e) {
            logger.warn("Last name field not found: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Enter phone number for registration
     */



    /**
     * Enter password for new account creation
     */
    public LoginPage enterRegistrationPassword(String password) {
        try {
            ElementActions.waitForVisibility(registerPasswordInput);
            ElementActions.type(registerPasswordInput, password);
            logger.info("Entered registration password");
        } catch (Exception e) {
            logger.warn("Password input not found: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Check terms and conditions checkbox
     */



    /**
     * Click Create Account / Register button
     */
    public LoginPage clickCreateAccount() {
        try {
            ElementActions.click(createAccountButton);
            ElementActions.sleep(2000);
            logger.info("Clicked Create Account button");
        } catch (Exception e) {
            logger.error("Create Account button not found: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Complete registration flow for new user
     */
    public LoginPage register(String email, String password, String firstName, String lastName) {
        logger.info("Performing registration for email: {}", email);
        return openSignInModal()
                .selectEmailOption()
                .submitEmail(email)
                .enterFirstName(firstName)
                .enterLastName(lastName)
                .enterRegistrationPassword(password)
                .clickCreateAccount();
    }

    // ========== OTP VERIFICATION ==========

    /**
     * Enter OTP code
     */
    public LoginPage enterOtpCode(String otp) {
        try {
            if (ElementActions.isDisplayed(otpInput, 5)) {
                ElementActions.type(otpInput, otp);
                logger.info("Entered OTP code");
            }
        } catch (Exception e) {
            logger.debug("OTP input not present");
        }
        return this;
    }

    /**
     * Click Verify button for OTP
     */
    public LoginPage clickVerify() {
        try {
            ElementActions.click(verifyButton);
            logger.info("Clicked Verify button");
        } catch (Exception e) {
            logger.debug("Verify button not present");
        }
        return this;
    }

    // ========== ACCOUNT / LOGOUT ==========

    /**
     * Click user account dropdown
     */
    public LoginPage openAccountDropdown() {
        try {
            ElementActions.click(userAccountDropdown);
            logger.info("Opened account dropdown");
        } catch (Exception e) {
            logger.warn("Account dropdown not found: {}", e.getMessage());
        }
        return this;
    }

    /**
     * Click Logout button
     */
    public HomePage clickLogout() {
        try {
            openAccountDropdown();
            ElementActions.click(logoutButton);
            ElementActions.sleep(1000);
            logger.info("Clicked Logout");
        } catch (Exception e) {
            logger.warn("Logout button not found: {}", e.getMessage());
        }
        return new HomePage();
    }

    // ========== VERIFICATION METHODS ==========

    /**
     * Check if login was successful
     */
    public boolean isLoginSuccessful() {
        boolean success = WaitUtils.waitForUrlToContain("my-account") ||
                ElementActions.isDisplayed(welcomeMessage, 5) ||
                ElementActions.isDisplayed(userAccountDropdown, 5);
        logger.info("Login successful: {}", success);
        return success;
    }

    /**
     * Check if registration was successful
     */

    /**
     * Check if error message is displayed
     */
    public boolean isErrorMessageDisplayed() {
        boolean hasError = ElementActions.isDisplayed(emailErrorMessage, 2) ||
                ElementActions.isDisplayed(loginErrorMessage, 2);
        logger.info("Error message displayed: {}", hasError);
        return hasError;
    }

    /**
     * Get error message text
     */
    public String getErrorMessage() {
        try {
            if (ElementActions.isDisplayed(emailErrorMessage, 2)) {
                return ElementActions.getText(emailErrorMessage);
            }
            if (ElementActions.isDisplayed(loginErrorMessage, 2)) {
                return ElementActions.getText(loginErrorMessage);
            }
        } catch (Exception e) {
            logger.debug("No error message found");
        }
        return "";
    }

    /**
     * Check if sign-in modal is currently open
     */
    public boolean isModalOpen() {
        return ElementActions.isDisplayed(signInModalTitle, 3);
    }

    /**
     * Get the page title after login/registration
     */
    public String getPageTitle() {
        return DriverFactory.getPageTitle();
    }

    /**
     * Check if currently on password entry step (existing user)
     */
    public boolean isPasswordStep() {
        return ElementActions.isDisplayed(passwordInput, 3) &&
                ElementActions.isDisplayed(signInSubmitButton, 3);
    }

    /**
     * Check if currently on registration form step (new user)
     */
    public boolean isRegistrationStep() {
        return ElementActions.isDisplayed(firstNameInput, 3) ||
                ElementActions.isDisplayed(createAccountButton, 3);
    }
}
