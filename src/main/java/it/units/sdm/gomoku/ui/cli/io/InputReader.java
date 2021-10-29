package it.units.sdm.gomoku.ui.cli.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class InputReader {  // TODO : to be deleted 

    private static final InputReader singleInstance = new InputReader();
    private static final InputStream DEFAULT_INPUT_STREAM = System.in;
    private static final Scanner scanner = new Scanner(DEFAULT_INPUT_STREAM);

    public static InputReader getInstance() {
        return singleInstance;
    }

    public static <T> T checkInputAndGet(
            @NotNull final Supplier<T> inputSupplier,
            @NotNull final Predicate<T> validator,
            @NotNull final PrintStream out,
            @NotNull final String messageErrorIfInvalid) throws IOException {
        return checkInputAndGet(
                Objects.requireNonNull(inputSupplier),
                Objects.requireNonNull(validator),
                Objects.requireNonNull(messageErrorIfInvalid),
                Objects.requireNonNull(out),
                IllegalArgumentException.class);
    }

    public static <T> T checkInputAndGet(
            @NotNull final Supplier<T> inputSupplier,
            @NotNull final Predicate<T> validator,
            @NotNull final String messageErrorIfInvalid,
            @NotNull final PrintStream out,
            @NotNull final Class<? extends Throwable> throwable) throws IOException {
        T inputValue = null;
        boolean isValidInput = false;
        while (!isValidInput) {
            try {
                inputValue = inputSupplier.get();
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

    public String nextLine() {
        return scanner.nextLine();
    }

    public int getAnIntFromInput() {
        try {
            return Integer.parseInt(scanner.nextLine().replace("\n", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
