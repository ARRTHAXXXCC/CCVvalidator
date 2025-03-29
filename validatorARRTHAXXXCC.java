import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.YearMonth;
import java.util.regex.Pattern;

public class CreditCardValidator {

    // ANSI Color Codes
    public static final String RED = "\u001B[31m";
    public static final String BLUE = "\u001B[34m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RESET = "\u001B[0m";

    public static void main(String[] args) {
        // Display legal disclaimer and banner
        System.out.println(RED + "*******************************************");
        System.out.println("*          MADE BY ARRTHAXXXCC            *");
        System.out.println("*******************************************");
        System.out.println(YELLOW + "LEGAL DISCLAIMER: This script is provided for educational purposes only.");
        System.out.println("Unauthorized use of credit card information may violate laws.");
        System.out.println("Use responsibly and only with explicit permission from cardholders." + RESET);
        System.out.println();

        // Get file path from user
        System.out.print(BLUE + "Enter the path to your combolist file: " + RESET);
        String filePath = System.console().readLine();  // Input file path

        // Read and validate cards from the file
        validateCardsFromFile(filePath);
    }

    // Validate cards from the combolist file
    public static void validateCardsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int validCount = 0, invalidCount = 0;

            while ((line = br.readLine()) != null) {
                // Extract details from the line
                String[] cardDetails = line.split("\\|");
                if (cardDetails.length != 3) {
                    continue; // Skip lines with incorrect format
                }

                String cardNumber = cardDetails[0].replaceAll("\\s", "");  // Remove spaces
                String expDate = cardDetails[1].trim();
                String cvv = cardDetails[2].trim();

                // Check if card is valid
                boolean isValid = validateCard(cardNumber, expDate, cvv);

                if (isValid) {
                    validCount++;
                    System.out.println(GREEN + "Valid Card: " + RESET + cardNumber + " | Exp: " + expDate + " | CVV: " + cvv);
                } else {
                    invalidCount++;
                    System.out.println(RED + "Invalid Card: " + RESET + cardNumber + " | Exp: " + expDate + " | CVV: " + cvv);
                }
            }

            // Output the result summary
            System.out.println("\n" + GREEN + validCount + " valid cards found." + RESET);
            System.out.println(RED + invalidCount + " invalid cards found." + RESET);

        } catch (IOException e) {
            System.out.println(RED + "Error reading the file. Please check the file path." + RESET);
        }
    }

    // Validate the card details: Luhn Check, Expiration Date, and CVV
    public static boolean validateCard(String cardNumber, String expDate, String cvv) {
        String cardType = identifyCardType(cardNumber);

        boolean isValidLuhn = luhnCheck(cardNumber);
        boolean isValidExp = validateExpirationDate(expDate);
        boolean isValidCvv = validateCVV(cvv, cardType);

        return isValidLuhn && isValidExp && isValidCvv;
    }

    // Validate the Luhn Algorithm
    public static boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));
            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            alternate = !alternate;
        }
        return sum % 10 == 0;
    }

    // Identify the card type based on patterns
    public static String identifyCardType(String cardNumber) {
        if (cardNumber.startsWith("4")) {
            return "Visa";
        } else if (Pattern.matches("^5[1-5]", cardNumber)) {
            return "MasterCard";
        } else if (Pattern.matches("^3[47]", cardNumber)) {
            return "American Express";
        } else if (cardNumber.startsWith("6011") || cardNumber.startsWith("65")) {
            return "Discover";
        }
        return "Unknown";
    }

    // Validate expiration date (MM/YY format)
    public static boolean validateExpirationDate(String expDate) {
        if (!expDate.matches("^(0[1-9]|1[0-2])/[0-9]{2}$")) {
            return false;
        }
        String[] parts = expDate.split("/");
        int month = Integer.parseInt(parts[0]);
        int year = Integer.parseInt(parts[1]) + 2000;

        YearMonth current = YearMonth.now();
        YearMonth exp = YearMonth.of(year, month);

        return exp.isAfter(current);
    }

    // Validate CVV based on card type
    public static boolean validateCVV(String cvv, String cardType) {
        if (!cvv.matches("\\d+")) return false;
        int length = cvv.length();

        if (cardType.equals("American Express")) {
            return length == 4;
        } else { // Visa, MasterCard, Discover
            return length == 3;
        }
    }
}
