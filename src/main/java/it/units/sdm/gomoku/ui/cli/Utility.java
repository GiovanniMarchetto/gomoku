package it.units.sdm.gomoku.ui.cli;

import java.util.Scanner;

public class Utility {

    public static boolean isValidCharInsertedFromStdInCaseInsensitive(char...validChars) {
        try (Scanner userInput = new Scanner(System.in)) {
            //noinspection CatchMayIgnoreException
            try {
                char inserted = userInput.nextLine().toLowerCase().charAt(0);
                for(char validChar : validChars) {
                    if(Character.toLowerCase(inserted) == Character.toLowerCase(validChar)) {
                        return true;
                    }
                }
            } catch (Exception invalidInputException) {
            }
            return false;
        }
    }

}
