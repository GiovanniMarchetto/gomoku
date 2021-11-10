package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.ui.support.MatchTypes;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CLIStartViewTest {
    //TODO : missing tests

    private final static String ERROR_STRING = "ERROR";
    private static final ByteArrayOutputStream fakeStdOut = new ByteArrayOutputStream();
    private final static Logger loggerThisClass = Logger.getLogger(CLIStartViewTest.class.getCanonicalName());
    private final CLIStartView cliStartView = new CLIStartView(new StartViewmodel(new CLIMainViewmodel()));

    @BeforeAll
    static void setStdOutToFakeOut() {
        System.setOut(new PrintStream(fakeStdOut));
    }

    @ParameterizedTest
    @CsvSource({"-1," + ERROR_STRING,
            "0,CPU_VS_CPU",
            "1,PERSON_VS_CPU",
            "2,PERSON_VS_PERSON",
            "3," + ERROR_STRING})
    void chooseMatchTypeAccordingToNumberOfPlayerReceivedFromInput(
            String inputProvidedByUser, String matchTypeOrError) {
        System.setIn(new ByteArrayInputStream((inputProvidedByUser + System.lineSeparator()).getBytes()));
        try {
            Supplier<Boolean> hasModelNotifiedTheUserAboutInvalidInput = () -> fakeStdOut.size() > 0;
            fakeStdOut.reset();
            try {
                MatchTypes matchTypeAccordingToInputInsertedByTheUser =
                        (MatchTypes) TestUtility
                                .getMethodAlreadyMadeAccessible(cliStartView.getClass(), "askAndGetNumberOfPlayers")
                                .invoke(null);
                assertEquals(MatchTypes.valueOf(matchTypeOrError), matchTypeAccordingToInputInsertedByTheUser);
            } catch (InvocationTargetException invalidInputInsertedCausedException) {
                if (hasModelNotifiedTheUserAboutInvalidInput.get()) {
                    assertEquals(ERROR_STRING, matchTypeOrError);
                } else {
                    throw invalidInputInsertedCausedException;
                }
            }
        } catch (Exception e) {
            fail(e);
        }

    }

}