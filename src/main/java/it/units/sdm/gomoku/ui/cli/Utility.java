package it.units.sdm.gomoku.ui.cli;

import java.util.Scanner;

public class Utility {

    public static boolean isValidCharInsertedCaseInsensitive(char insertedByTheUser, char validChar) {
        return Character.toLowerCase(insertedByTheUser) == Character.toLowerCase(validChar);
    }

    public static boolean isYInsertedFromStdIn() {
        try (Scanner userInput = new Scanner(System.in)) {
            try {
                char inserted = userInput.nextLine().toLowerCase().charAt(0);
                return isValidCharInsertedCaseInsensitive(inserted, 'Y');
            } catch (Exception invalidInputException) {
                return false;
            }
        }
    }

    public static boolean isValidCharInsertedFromStdIn(char validChar) {
        try (Scanner userInput = new Scanner(System.in)) {
            try {
                char inserted = userInput.nextLine().toLowerCase().charAt(0);
                return isValidCharInsertedCaseInsensitive(inserted, validChar);
            } catch (Exception invalidInputException) {
                return false;
            }
        }
    }

}
