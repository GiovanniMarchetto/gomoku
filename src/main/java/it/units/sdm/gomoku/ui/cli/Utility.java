package it.units.sdm.gomoku.ui.cli;

import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utility {
    public static char getLowercaseCharIfValidCaseInsensitiveOr0(char... validChars) {
        Scanner userInput = new Scanner(System.in);
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

    public static int getAIntFromStdIn() {
        Scanner fromUser = new Scanner(System.in);
        int aInt = 0;
        boolean validInputInserted = false;
        do {
            try {
                if (fromUser.hasNextLine()) {
                    aInt = Integer.parseInt(fromUser.nextLine().trim());
                    validInputInserted = true;
                } else {
                    throw new NoSuchElementException("The scanner has not any other token");
                }
            } catch (NoSuchElementException e) {
                Logger.getLogger(Utility.class.getCanonicalName())
                        .log(Level.SEVERE, "Error when reading an int from StdIn, 0 returned", e);
                return 0;
            } catch (Exception e) {
                System.err.print("Integer value expected. Try again: ");
                System.err.flush();
            }
        } while (!validInputInserted);
        return aInt;
    }
}
