package it.units.sdm.gomoku.ui.cli;

import java.util.Scanner;

public class Utility {

    public static boolean isValidCharInsertedFromStdIn(char validChar) {
        try (Scanner userInput = new Scanner(System.in)) {
            try {
                char inserted = userInput.nextLine().toLowerCase().charAt(0);
                return Character.toLowerCase(inserted) == Character.toLowerCase(validChar);
            } catch (Exception invalidInputException) {
                return false;
            }
        }
    }

}
