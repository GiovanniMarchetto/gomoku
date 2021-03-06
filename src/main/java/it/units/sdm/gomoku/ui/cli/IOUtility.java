package it.units.sdm.gomoku.ui.cli;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IOUtility {

    public static char getLowercaseCharIfValidCaseInsensitiveOr0(char... validChars) {
        //noinspection CatchMayIgnoreException
        try {
            char inserted = SettableScannerSingleton
                    .createNewScannerForSystemInIfAllowedOrUseTheDefaultAndGet()
                    .nextLine().toLowerCase().charAt(0);
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
        Scanner fromUser = SettableScannerSingleton
                .createNewScannerForSystemInIfAllowedOrUseTheDefaultAndGet();
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
                Logger.getLogger(IOUtility.class.getCanonicalName())
                        .log(Level.SEVERE, "Error when reading an int from StdIn, 0 returned", e);
                return 0;
            } catch (Exception e) {
                System.err.print("Integer value expected. Try again: ");
                System.err.flush();
            }
        } while (!validInputInserted);
        return aInt;
    }

    public static char getLowercaseCharWhenValidCaseInsensitiveOrCycle(char... validChars) {
        char aChar;
        boolean validInputInserted;
        do {
            aChar = getLowercaseCharIfValidCaseInsensitiveOr0(validChars);
            validInputInserted = aChar != 0;
            if (!validInputInserted) {
                System.out.print("Invalid input, please insert one from the list " +
                        Arrays.toString(validChars) + ": ");
            }
        } while (!validInputInserted);
        return aChar;
    }

    public static String checkInputAndGet(
            @NotNull final Predicate<String> validator,
            @NotNull final String messageErrorIfInvalid,
            @NotNull final PrintStream out,
            @NotNull final Class<? extends Throwable> throwable) {
        Scanner fromUser = SettableScannerSingleton
                .createNewScannerForSystemInIfAllowedOrUseTheDefaultAndGet();
        String inputValue = null;
        boolean isValidInput = false;
        while (!isValidInput) {
            try {
                inputValue = fromUser.nextLine();
            } catch (InputMismatchException ignored) {
            } catch (Exception e) {
                if (!throwable.isAssignableFrom(e.getClass())) {
                    throw e;
                }
            }
            isValidInput = validator.test(inputValue);
            if (!isValidInput) {
                Objects.requireNonNull(out).print("Invalid input. " + messageErrorIfInvalid);
            }
        }
        return inputValue;
    }

    public static String checkInputAndGet(
            @NotNull final Predicate<String> validator,
            @NotNull final PrintStream out,
            @NotNull final String messageErrorIfInvalid) {
        return checkInputAndGet(
                Objects.requireNonNull(validator),
                Objects.requireNonNull(messageErrorIfInvalid),
                Objects.requireNonNull(out),
                IllegalArgumentException.class);
    }

    public static boolean isYesFromStdin() {
        return getLowercaseCharWhenValidCaseInsensitiveOrCycle('y', 'n') == 'y';
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(Objects.requireNonNull(s));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static class SettableScannerSingleton {

        private SettableScannerSingleton() {
        }

        public static Scanner createNewScannerForSystemInIfAllowedOrUseTheDefaultAndGet() {
            // for debugging purposes, access with reflection
            @Nullable Scanner scannerSingleInstance = new Scanner(System.in);
            return scannerSingleInstance;
        }

    }
}
