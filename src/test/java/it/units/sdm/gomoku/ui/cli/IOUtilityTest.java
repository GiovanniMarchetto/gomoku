package it.units.sdm.gomoku.ui.cli;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class IOUtilityTest { // TODO: a class with a similar name is present in src/test/java/it/units/sdm/gomoku/model/utils

    @ParameterizedTest
    @CsvSource({"a, a, a", "_, a, b", "a, a, A", "a, a, b#a#c", "a, a, b#A#c", "_, a, b#d#c"})
    void getLowercaseCharIfValidLowerCaseOr0(char expected, char insertedInput, String validInputs) {
        if (expected == '_') {
            expected = 0;
        }
        String[] validInputStringArray = validInputs.split("#");
        char[] validInputCharArray = new char[validInputStringArray.length];
        for (int i = 0; i < validInputStringArray.length; i++) {
            validInputCharArray[i] = validInputStringArray[i].charAt(0);
        }

        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(new byte[]{(byte) insertedInput})) {
            System.setIn(fakeStdIn);
            assertEquals(expected, IOUtility.getLowercaseCharIfValidCaseInsensitiveOr0(validInputCharArray));
        } catch (IOException e) {
            e.printStackTrace();
            fail();
        }
    }

    @ParameterizedTest
    @CsvSource({"1,1", "2,2", "4, dfs fds 3# 4", "5,### # 5"})
    void getAIntFromStdIn(int expected, String inserted) {
        inserted = inserted.replaceAll("#", System.lineSeparator());
        PrintStream stdErr = System.err;
        disableStdErr();    // avoid seeing what the tested method prints to stdErr
        try (ByteArrayInputStream fakeStdIn = new ByteArrayInputStream(inserted.getBytes())) {
            System.setIn(fakeStdIn);
            assertEquals(expected, IOUtility.getAIntFromStdIn());
        } catch (IOException e) {
            rehabilitateStdErr(stdErr);
            e.printStackTrace();
            fail();
        }
    }

    private void rehabilitateStdErr(PrintStream realStdErr) {
        System.setErr(realStdErr);
    }

    private void disableStdErr() {
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }

}