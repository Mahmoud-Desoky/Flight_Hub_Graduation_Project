package com.flighthub.utils;

import com.github.javafaker.Faker;

/**
 * Utility class for generating fake test data using JavaFaker.
 * Useful for creating registration data, booking information, etc.
 */
public class TestDataGenerator {

    private static final Faker faker = new Faker();

    private TestDataGenerator() {
        // Utility class
    }

    public static String getRandomEmail() {
        return faker.internet().emailAddress();
    }

    public static String getRandomPassword() {
        return faker.internet().password(10, 15, true, true, true);
    }

    public static String getRandomFirstName() {
        return faker.name().firstName();
    }

    public static String getRandomLastName() {
        return faker.name().lastName();
    }

    public static String getRandomFullName() {
        return faker.name().fullName();
    }

    public static String getRandomPhoneNumber() {
        return faker.phoneNumber().cellPhone();
    }

    public static String getRandomAddress() {
        return faker.address().fullAddress();
    }

    public static String getRandomCity() {
        return faker.address().city();
    }

    public static String getRandomCountry() {
        return faker.address().country();
    }

    public static String getRandomPostalCode() {
        return faker.address().zipCode();
    }

    public static String getRandomBirthDate() {
        return faker.date().birthday(18, 65).toString();
    }

    public static String getRandomCompany() {
        return faker.company().name();
    }

    public static String getRandomCreditCardNumber() {
        return faker.finance().creditCard();
    }

    public static String getRandomFlightOrigin() {
        String[] origins = {"New York", "Los Angeles", "Chicago", "Toronto", "Vancouver",
                "London", "Paris", "Tokyo", "Singapore", "Sydney"};
        return origins[faker.random().nextInt(origins.length)];
    }

    public static String getRandomFlightDestination() {
        String[] destinations = {"Miami", "San Francisco", "Boston", "Montreal", "Calgary",
                "Berlin", "Rome", "Dubai", "Bangkok", "Auckland"};
        return destinations[faker.random().nextInt(destinations.length)];
    }

    /**
     * Generate a unique email with timestamp to avoid duplicates
     */
    public static String getUniqueEmail() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String localPart = "test" + timestamp.substring(timestamp.length() - 6);
        return localPart + "@testmail.com";
    }

    /**
     * Generate a strong password that meets common requirements
     */
    public static String getStrongPassword() {
        String upper = faker.letterify("?").toUpperCase();
        String lower = faker.letterify("???").toLowerCase();
        String digits = faker.numerify("###");
        String special = "!@#$";
        return upper + lower + digits + special.charAt(faker.random().nextInt(special.length()));
    }
}
