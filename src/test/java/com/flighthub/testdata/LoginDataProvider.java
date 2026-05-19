package com.flighthub.testdata;

import com.flighthub.utils.TestDataGenerator;
import org.testng.annotations.DataProvider;

/**
 * TestNG DataProvider class for Login and Registration test data.
 * Provides various data sets for positive and negative test scenarios.
 */
public class LoginDataProvider {

    private static final String VALID_REGISTERED_EMAIL = "test.user@example.com";
    private static final String VALID_REGISTERED_PASSWORD = "TestPassword123!";

    /**
     * DataProvider for valid login credentials
     */
    @DataProvider(name = "validLoginData")
    public Object[][] validLoginData() {
        return new Object[][]{
                // {email, password}
                {VALID_REGISTERED_EMAIL, VALID_REGISTERED_PASSWORD}
        };
    }

    /**
     * DataProvider for invalid login credentials
     */
    @DataProvider(name = "invalidLoginData")
    public Object[][] invalidLoginData() {
        return new Object[][]{
                // {email, password, expectedError}
                {"invalid@email.com", "wrongpassword", "Invalid credentials"},
                {"notfound@test.com", "Password123!", "account not found"},
                {"", "password123", "Email is required"},
                {"test@email.com", "", "Password is required"},
                {"notanemail", "password123", "Invalid email format"},
                {"test@email.com", "short", "Password too short"},
                {VALID_REGISTERED_EMAIL, "wrongpassword123", "Incorrect password"}
        };
    }

    /**
     * DataProvider for login edge cases
     */
    @DataProvider(name = "loginEdgeCases")
    public Object[][] loginEdgeCases() {
        return new Object[][]{
                // {email, description}
                {"user+tag@example.com", "Email with plus sign"},
                {"user.name@example.co.uk", "Email with subdomain"},
                {"user123@example.com", "Email with numbers"},
                {"USER@EXAMPLE.COM", "Email uppercase"},
        };
    }

    /**
     * DataProvider for valid registration data
     */
    @DataProvider(name = "validRegistrationData")
    public Object[][] validRegistrationData() {
        String uniqueEmail = TestDataGenerator.getUniqueEmail();
        String strongPassword = TestDataGenerator.getStrongPassword();
        String firstName = TestDataGenerator.getRandomFirstName();
        String lastName = TestDataGenerator.getRandomLastName();

        return new Object[][]{
                // {email, password, firstName, lastName, phone}
                {uniqueEmail, strongPassword, firstName, lastName, "+1234567890"},
        };
    }

    /**
     * DataProvider for invalid registration data
     */
    @DataProvider(name = "invalidRegistrationData")
    public Object[][] invalidRegistrationData() {
        return new Object[][]{
                // {email, password, firstName, lastName, expectedError}
                {"", "Password123!", "John", "Doe", "Email is required"},
                {"invalidemail", "Password123!", "John", "Doe", "Invalid email format"},
                {"test@test.com", "", "John", "Doe", "Password is required"},
                {"test@test.com", "123", "John", "Doe", "Password too short"},
                {"test@test.com", "password", "John", "Doe", "Password needs uppercase"},
                {"test@test.com", "PASSWORD", "John", "Doe", "Password needs lowercase"},
                {"test@test.com", "Password", "John", "Doe", "Password needs number"},
                {"test@test.com", "Password123", "", "Doe", "First name is required"},
                {"test@test.com", "Password123", "John", "", "Last name is required"},
                {VALID_REGISTERED_EMAIL, "Password123!", "John", "Doe", "Email already registered"}
        };
    }

    /**
     * DataProvider for registration password validation
     */
    @DataProvider(name = "registrationPasswordValidation")
    public Object[][] registrationPasswordValidation() {
        return new Object[][]{
                // {password, expectedValidation}
                {"Abcdef1!", "Valid password"},
                {"abcdefgh", "Missing uppercase and number"},
                {"ABCDEFGH", "Missing lowercase and number"},
                {"12345678", "Missing letters"},
                {"Abcdefgh", "Missing number and special char"},
                {"Ab1!", "Too short"},
                {"Abcdefgh123!@#", "Valid strong password"}
        };
    }

    /**
     * DataProvider for social login options
     */
    @DataProvider(name = "socialLoginProviders")
    public Object[][] socialLoginProviders() {
        return new Object[][]{
                // {provider, expectedUrlContains}
                {"google", "google"},
                {"apple", "apple"}
        };
    }
}
