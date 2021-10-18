package it.units.sdm.gomoku.ui.cli.io;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OutputPrinter extends OutputStream {        // TODO : to be tested

    private final static ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final static String ANSI_CURSOR_ONE_LINE_UP = "\u001B[A";
    private final static String ANSI_CURSOR_ONE_CHAR_RIGHT = "\u001B[C";
    private static final Charset DEFAULT_OUTPUT_CHARSET = Charset.defaultCharset();
    private static OutputPrinter singleInstance;
    private final Charset outputCharset;
    private final PrintStream standardOutput;
    private final List<Integer> numberOfPrintedBytesInSection;
    private final List<Integer> newLinePositions;
    private int totalNumberOfPrintedBytes;

    private OutputPrinter() {
        this.outputCharset = DEFAULT_OUTPUT_CHARSET;
        this.numberOfPrintedBytesInSection = new CopyOnWriteArrayList<>();
        this.newLinePositions = new CopyOnWriteArrayList<>();
        this.standardOutput = System.out;
        this.totalNumberOfPrintedBytes = 0;
    }

    public static OutputPrinter getInstance() {
        if (singleInstance == null) {
            singleInstance = new OutputPrinter();
        }
        return singleInstance;
    }

    private static <T> void removeLastItemOf(@NotNull final List<T> list) {
        throwIfEmpty(Objects.requireNonNull(list)).remove(getLastIndexOf(list));
    }

    private static void moveCursorAtTheEndOfThePreviousLine(
            @NotNull final StringBuilder whereToAppendCharacters,
            int numberOfCharactersInThePreviousLine) {
        Objects.requireNonNull(whereToAppendCharacters).append(ANSI_CURSOR_ONE_LINE_UP);
        whereToAppendCharacters.append(
                ANSI_CURSOR_ONE_CHAR_RIGHT.repeat(Math.max(0, numberOfCharactersInThePreviousLine))
        );
    }

    synchronized private static <T> int getLastIndexOf(@NotNull final List<T> list) {
        return throwIfEmpty(Objects.requireNonNull(list)).size() - 1;
    }

    synchronized private static <T> List<T> throwIfEmpty(@NotNull List<T> list) {
        if (Objects.requireNonNull(list).isEmpty()) {
            throw new IndexOutOfBoundsException("Empty list");
        }
        return list;
    }

    synchronized private static <T> T getLastItemOf(@NotNull final List<T> list) {
        return Objects.requireNonNull(list).get(getLastIndexOf(list));
    }

    synchronized public <T> void print(@NotNull final T toPrint) throws IOException {
        String filteredString = Objects.requireNonNull(toPrint).toString().replaceAll("\r", "");
        Matcher matcher = Pattern.compile("\n").matcher(filteredString);
        while (matcher.find()) {
            newLinePositions.add(totalNumberOfPrintedBytes + matcher.start());
        }
        outputStream.write(filteredString.getBytes(outputCharset));
        printToConsole();
    }

    public <T> void println(@NotNull final T toPrint) throws IOException {
        print(Objects.requireNonNull(toPrint) + "\n");
    }

    private void printToConsole() {
        standardOutput.print(outputStream.toString(outputCharset));
        int numberOfJustPrintedBytes = outputStream.size();
        totalNumberOfPrintedBytes += numberOfJustPrintedBytes;
        if (!numberOfPrintedBytesInSection.isEmpty()) {
            numberOfPrintedBytesInSection.set(
                    getLastIndexOf(numberOfPrintedBytesInSection),
                    getLastItemOf(numberOfPrintedBytesInSection) + numberOfJustPrintedBytes
            );
        }
        outputStream.reset();
        standardOutput.flush();
    }

    public void clearAll() {
        clearBytes(totalNumberOfPrintedBytes);
    }

    synchronized private void clearBytes(int numberOfBytesToClear) {
        if (numberOfBytesToClear > totalNumberOfPrintedBytes) {
            throw new IndexOutOfBoundsException("Cannot delete more bytes than written.");
        } else {
            int numberOfBackSpace;
            for (int i = numberOfBytesToClear; i > 0; i -= numberOfBackSpace) {
                int numberOfBytesUntilLastNewLineExcluded = totalNumberOfPrintedBytes -
                        getPositionOfPreviousNewLineCharacter().orElse(0) - 1;
                numberOfBackSpace = Math.min(i, numberOfBytesUntilLastNewLineExcluded);
                StringBuilder toPrintForClearing = new StringBuilder();
                toPrintForClearing.append(IntStream.range(0, numberOfBackSpace)
                        .mapToObj(j -> "\b \b")
                        .collect(Collectors.joining()));
                totalNumberOfPrintedBytes -= numberOfBackSpace;
                if (numberOfBackSpace != i && !newLinePositions.isEmpty()) {
                    int positionOfLastNewLineCharacter = getPositionOfPreviousNewLineCharacter().orElseThrow();
                    removeLastItemOf(newLinePositions);
                    int numberOfCharactersInThePreviousLine = positionOfLastNewLineCharacter -
                            getPositionOfPreviousNewLineCharacter().orElse(0) - 1;
                    moveCursorAtTheEndOfThePreviousLine(toPrintForClearing, numberOfCharactersInThePreviousLine);
                    i--;    // the new line character has been cleared by moving the cursor at the end of the previous line
                    totalNumberOfPrintedBytes--;
                }
                standardOutput.print(toPrintForClearing);
                standardOutput.flush();
            }
        }
    }

    private Optional<Integer> getPositionOfPreviousNewLineCharacter() {
        if (newLinePositions.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(getLastItemOf(newLinePositions));
        }
    }

    synchronized public void createNewSection() {
        numberOfPrintedBytesInSection.add(0);
    }

    synchronized public void clearLastSection() {
        clearBytes(getNumberOfBytesInLastSection());
        numberOfPrintedBytesInSection.remove(getLastIndexOf(numberOfPrintedBytesInSection));
    }

    synchronized private Integer getNumberOfBytesInLastSection() {
        return numberOfPrintedBytesInSection.get(getLastIndexOf(numberOfPrintedBytesInSection));
    }

    @Override
    public void write(int b) throws IOException {
        print(b);
    }
}