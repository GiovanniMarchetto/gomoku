package it.units.sdm.gomoku.ui.cli;

import java.util.Scanner;

public class Utility {

    public static char getLowercaseCharIfValidLowerCaseOr0(char... validChars) {
        try (Scanner userInput = new Scanner(System.in)) {
            //noinspection CatchMayIgnoreException
            try {
                char inserted = userInput.nextLine().toLowerCase().charAt(0);
                for (char validChar : validChars) {
                    if (Character.toLowerCase(inserted) == Character.toLowerCase(validChar)) {
                        return Character.toLowerCase(validChar);
                    }
                }
            } catch (Exception invalidInputException) {
            }
            return 0;
        }
    }

}
