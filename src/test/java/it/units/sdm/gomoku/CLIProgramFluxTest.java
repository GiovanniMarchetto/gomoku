package it.units.sdm.gomoku;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.StartViewmodel;
import it.units.sdm.gomoku.ui.UIUtility;
import it.units.sdm.gomoku.ui.cli.CLIMain;
import it.units.sdm.gomoku.ui.cli.CLISceneController;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.views.CLIMainView;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.beans.PropertyChangeEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Disabled   // TODO: tests to do
public class CLIProgramFluxTest {

    @NotNull
    private static final Supplier<CLISceneController> cliSceneControllerInstanceGetter = () -> {
        try {
            return (CLISceneController) TestUtility
                    .getMethodAlreadyMadeAccessible(CLISceneController.class, "getInstance")
                    .invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
            fail(e);
            return null;
        }
    };
    @NotNull
    private static final Logger loggerThisClass = Logger.getLogger(CLIProgramFluxTest.class.getCanonicalName());    // TODO : replace all Logger field with method invocation Utility.getLoggerOfClass(Class<?>)
    @NotNull
    private final static PipedOutputStream PIPED_OUTPUT_STREAM = new PipedOutputStream();   // TODO : rethink about this
    @NotNull
    private final static PipedInputStream PIPED_INPUT_STREAM = new PipedInputStream();
    @NotNull
    private final static Thread dataProducerThread = new Thread(() -> {
        try (
                PrintWriter pw = new PrintWriter(PIPED_OUTPUT_STREAM, true)
        ) {
            while (true) {
                pw.println("1");
            }
        }
    });
    @NotNull
    private final static InputStream realStdIn = System.in;
    @Nullable
    private StartViewmodel startViewmodel;
    @Nullable
    private MainViewmodel mainViewmodel;
    @Nullable
    private Match match;
    @Nullable
    private List<?> gameListOfTheMatch;
    @Nullable
    private Game firstGameOfMatch;
    @Nullable
    private Board boardOfFirstGame;

    @BeforeAll
    static void ignoreStdOutAndStdErr() {
//        System.setOut(new PrintStream(new ByteArrayOutputStream()));
//        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }

    @BeforeAll
    static void produceDataForStdInForever() {
        try {
            CLIProgramFluxTest.PIPED_OUTPUT_STREAM.connect(PIPED_INPUT_STREAM);
        } catch (IOException e) {
            loggerThisClass.log(Level.SEVERE, "Error when connecting the piped stream", e);
        }
        System.setIn(PIPED_INPUT_STREAM);
        dataProducerThread.start();
    }

    private static void setRealStdIn() {
        try {
            System.setIn(Objects.requireNonNull(realStdIn));
            TestUtility.getFieldAlreadyMadeAccessible(IOUtility.SettableScannerSingleton.class, "scannerCanBeModified")
                    .set(null, true);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            loggerThisClass.log(Level.SEVERE, "Unable to reset StdIn to its real value", e);
        }
    }

    @AfterAll
    static void stopProducingDataToStdIn() {
        dataProducerThread.interrupt();
    }

    @BeforeEach
    void connectStdInToPipedInputStream() {
        System.setIn(PIPED_INPUT_STREAM);
    }

    @Test
    void launchApplicationAndCheckSceneControllerInstantiation() {
        try {
            try {
                TestUtility.getMethodAlreadyMadeAccessible(CLIMain.class, "launch")
                        .invoke(null);
            } catch (InvocationTargetException noInputLinesButDontCare) {
                // TODO : rethink about this (Scanner throws exception because "No line found" and launch method is interrupted before selecting the right view)
            }
            assertTrue((boolean) TestUtility
                    .getMethodAlreadyMadeAccessible(CLISceneController.class, "wasAlreadyInstantiated")
                    .invoke(null));
        } catch (Exception e) {
            fail(e);
        }
    }

//    @Test
//        // TODO : not working - do we really need this test for the program flow testing?
//    void checkFirstView() { // TODO: refactor needed (problem of responsibility separation in the model?)
//
//        Supplier<View<?>> actualCurrentViewGetter = () -> {
//            try {
//                return (View<?>) TestUtility
//                        .getFieldAlreadyMadeAccessible(CLISceneController.class, "currentView")
//                        .get(null);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                fail(e);
//                return null;
//            }
//        };
//        AtomicReference<View<?>> actualCurrentViewAtomicReference = new AtomicReference<>(actualCurrentViewGetter.get());
//        Thread actualCurrentViewObserver = new Thread(() -> {
//            do {
//                actualCurrentViewAtomicReference.set(actualCurrentViewGetter.get());
//            } while (actualCurrentViewAtomicReference.get() == null);  // TODO : better to use properties? This loop simply observes the view to change
//        });
//        Thread actualCurrentViewObserverInterrupterIfThreadNotJoiningWithin1ms = new Thread(() -> {
//            try {
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//                loggerThisClass.log(Level.SEVERE, "Thread stopper interrupted", e);
//            }
//            actualCurrentViewObserver.interrupt();
//        });
//
//        new Thread(this::launchApplicationAndCheckSceneControllerInstantiation).start();
//        boolean atBeginningTheCurrentViewIsNull = actualCurrentViewAtomicReference.get() == null;
//        assert atBeginningTheCurrentViewIsNull;
//        actualCurrentViewObserver.start();
//        actualCurrentViewObserverInterrupterIfThreadNotJoiningWithin1ms.start();
//
//        try {
//            actualCurrentViewObserver.join();
//            actualCurrentViewObserverInterrupterIfThreadNotJoiningWithin1ms.join();
//            assertTrue(actualCurrentViewAtomicReference.get() instanceof CLIStartView);
//        } catch (InterruptedException e) {
//            fail(e);
//        }
//    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.ui.UIUtility#setupsSupplierAndFlagIfValid")
    void checkCorrectnessOfMatchCreationFromSetup(Setup setup) {    // TODO : what happens with invalid setup instance?
        launchApplicationAndCheckSceneControllerInstantiation();    // TODO : should call the immediately previous method
        try {
            setFieldsInStartViewmodelFromSetup(setup);
            assert this.startViewmodel != null;
            this.startViewmodel.createAndStartMatch();
            this.mainViewmodel = (MainViewmodel)
                    TestUtility.getFieldValue("cliMainViewmodel", cliSceneControllerInstanceGetter.get());
            assert this.mainViewmodel != null;
            this.match = Objects.requireNonNull((Match) TestUtility.getFieldValue("match", this.mainViewmodel));

            assertEquals(setup.player1(), this.match.getCurrentBlackPlayer());
            assertEquals(setup.player2(), this.match.getCurrentWhitePlayer());
            assertEquals(setup.numberOfGames(), new PositiveInteger(this.match.getTotalNumberOfGames()));
            assertEquals(setup.boardSize(), TestUtility.getFieldValue("boardSize", this.match));

            gameListOfTheMatch = (List<?>) Objects.requireNonNull(TestUtility.getFieldValue("gameList", this.match));
            final int numberOfGamesCreatedInMatchImmediatelyAfterStartNewGameMethodInvocationImmediatelyAfterMatchCreation = 1;
            assertEquals(
                    numberOfGamesCreatedInMatchImmediatelyAfterStartNewGameMethodInvocationImmediatelyAfterMatchCreation,
                    gameListOfTheMatch.size());
            this.firstGameOfMatch = (Game) gameListOfTheMatch.get(0);
            assertNotNull(this.firstGameOfMatch);
            this.boardOfFirstGame = firstGameOfMatch.getBoard();
            assertNotNull(this.boardOfFirstGame);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            fail(e);
        }
    }

    @AfterEach
    void resetRealStdIn() {
        setRealStdIn();
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.ui.UIUtility#setupsSupplierAndFlagIfValid")
    void checkMainViewmodelToObservePropertyChangeEventsFiredFromModelAsConsequenceOfGameStart(Setup setup) {    // TODO : what happens with invalid setup instance?
        boolean player1IsHuman = setup.player1() instanceof HumanPlayer;
        boolean player2IsHuman = setup.player2() instanceof HumanPlayer;
        if (player1IsHuman || player2IsHuman) {
            checkCorrectnessOfNewGameInitializationFromSetup(setup);
            try {
                boolean coordinatesRequiredToContinue = true;
                Object[] propertiesValuesThatShouldBeObserved;
                assert match != null;
                if (player1IsHuman) {
                    propertiesValuesThatShouldBeObserved =
                            new Object[]{Game.Status.STARTED, match.getCurrentBlackPlayer(), coordinatesRequiredToContinue};
                } else if (player2IsHuman) {
                    Coordinates lastMove = assertNumberOfMovesDoneUntilHereAndGetCoordinatesOfLastMoveOrThrow(1);
                    propertiesValuesThatShouldBeObserved =
                            new Object[]{Game.Status.STARTED, coordinatesRequiredToContinue, lastMove, match.getCurrentWhitePlayer()};
                } else {
                    throw new IllegalStateException("Test may need to be updated: one of two player expected to be human");
                }

                assertEquals(
                        propertiesValuesThatShouldBeObserved.length,
                        getObservedPropertiesByMainViewmodel()
                                .stream()
                                .filter(propertyObserver -> Arrays.stream(propertiesValuesThatShouldBeObserved)
                                        .anyMatch(propVal -> {
                                            try {
                                                PropertyChangeEvent lastObservedChange = (PropertyChangeEvent) TestUtility
                                                        .getFieldValue("lastObservedEvt", propertyObserver);
                                                return lastObservedChange != null &&
                                                        propVal.equals(lastObservedChange.getNewValue());
                                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                                loggerThisClass.log(Level.SEVERE, "Error with reflections", e);
                                                return false;
                                            }
                                        }))
                                .count());

            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | NoSuchElementException e) {
                fail(e);
            }
        }
    }

    private Coordinates assertNumberOfMovesDoneUntilHereAndGetCoordinatesOfLastMoveOrThrow(
            int expectedNumberOfMovesUntilHere) throws NoSuchElementException, NoSuchFieldException, IllegalAccessException {
        assert boardOfFirstGame != null;
        @SuppressWarnings("ConstantConditions") // field declared as int
        int numberOfFilledPositionOnTheBoard = (int) TestUtility.getFieldValue("numberOfFilledPositions", boardOfFirstGame);
        assert numberOfFilledPositionOnTheBoard == expectedNumberOfMovesUntilHere;
        return IntStream.range(0, boardOfFirstGame.getSize())
                .unordered().parallel().boxed()
                .flatMap(i -> IntStream.range(0, boardOfFirstGame.getSize())
                        .mapToObj(j -> new Coordinates(i, j))
                        .filter(coord -> boardOfFirstGame.getCellAtCoordinatesOrNullIfInvalid(coord) != null))
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("Last move not found"));
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.ui.UIUtility#setupsSupplierAndFlagIfValid")
    void checkMainViewToObservePropertyChangeEventsFiredFromViewmodelAsConsequenceOfGameStart(Setup setup) {
        // TODO : REFACTOR, test is very similar to the previous one
        boolean player1IsHuman = setup.player1() instanceof HumanPlayer;
        boolean player2IsHuman = setup.player2() instanceof HumanPlayer;
        if (player1IsHuman || player2IsHuman) {
            checkCorrectnessOfNewGameInitializationFromSetup(setup);
            try {
                boolean userMustPlaceStone = true;
                Object[] propertiesValuesThatShouldBeObserved;
                assert mainViewmodel != null;
                if (player1IsHuman) {
                    propertiesValuesThatShouldBeObserved =
                            new Object[]{Game.Status.STARTED, mainViewmodel.getCurrentBlackPlayer(), userMustPlaceStone};
                } else if (player2IsHuman) {
                    Coordinates lastMove = assertNumberOfMovesDoneUntilHereAndGetCoordinatesOfLastMoveOrThrow(1);
                    propertiesValuesThatShouldBeObserved =
                            new Object[]{Game.Status.STARTED, userMustPlaceStone, lastMove, mainViewmodel.getCurrentWhitePlayer()};
                } else {
                    throw new IllegalStateException("Test may need to be updated: one of two player expected to be human");
                }

                //noinspection unchecked    // TODO : resee this
                assertEquals(
                        propertiesValuesThatShouldBeObserved.length,
                        getPropertiesObservedByView(
                                ((View<?>) ((List<?>) ((Map<?, ?>)
                                        Objects.requireNonNull(
                                                TestUtility.getFieldValue(
                                                        "historyOfCreatedViews", cliSceneControllerInstanceGetter.get())))
                                        .get(CLISceneController.CLIViewName.CLI_MAIN_VIEW)).get(0)))
                                .stream()
                                .filter(propertyObserver -> Arrays.stream(propertiesValuesThatShouldBeObserved)
                                        .anyMatch(propVal -> {
                                            try {
                                                PropertyChangeEvent lastObservedChange = (PropertyChangeEvent) TestUtility
                                                        .getFieldValue("lastObservedEvt", propertyObserver);
                                                return lastObservedChange != null &&
                                                        propVal.equals(lastObservedChange.getNewValue());
                                            } catch (NoSuchFieldException | IllegalAccessException e) {
                                                loggerThisClass.log(Level.SEVERE, "Error with reflections", e);
                                                return false;
                                            }
                                        }))
                                .count());

            } catch (NoSuchFieldException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                fail(e);
            }
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.ui.UIUtility#setupsSupplierAndFlagIfValid")
    void checkCorrectnessOfNewGameInitializationFromSetup(Setup setup) {    // TODO : what happens with invalid setup instance?
        checkCorrectnessOfMatchCreationFromSetup(setup);
        try {
            Player firstPlayerExpected = setup.player1();
            assert this.match != null;
            assertEquals(firstPlayerExpected, this.match.getCurrentBlackPlayer());

            assert mainViewmodel != null;
            Game firstGameOfMatch = (Game) TestUtility.getFieldValue("currentGame", mainViewmodel);
            assert this.firstGameOfMatch != null;
            assertEquals(this.firstGameOfMatch, firstGameOfMatch);

            Board boardOfFirstGame = (Board) TestUtility.getFieldValue("currentBoard", mainViewmodel);
            assert this.firstGameOfMatch != null;
            assertEquals(this.boardOfFirstGame, boardOfFirstGame);

            assert firstGameOfMatch != null;
            assertEquals(firstPlayerExpected, firstGameOfMatch.getCurrentPlayerProperty().getPropertyValue());
            assertEquals(Color.BLACK, firstGameOfMatch.getColorOfPlayer(firstPlayerExpected));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvSource({"3,4"})
        // TODO : use @MethodSource to test all possible coordinates
    void checkModelToReceiveCoordinatesForAMoveAsInsertedFromTheView(int xCoord, int yCoord) {
        // TODO : rethink this test
        Setup setup = (Setup) Objects.requireNonNull(
                UIUtility.setupsSupplierAndFlagIfValid()
                        .findFirst()
                        .orElseThrow()).get()[0];
        Thread initializeGame = new Thread(() -> {
            checkCorrectnessOfNewGameInitializationFromSetup(setup);
        });
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                initializeGame.interrupt();
            }
        }, 10); // TODO : should interrupt when cli ask coordinates to the user (anyway refactor to avoid magic numbers)
        initializeGame.start();
        try {
            initializeGame.join();
        } catch (InterruptedException alreadyInterrupted) {
        }

        View<?> mainView = null;
        try {
            mainView = getCurrentView();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
        assert mainView instanceof CLIMainView;
        ByteArrayInputStream fakeInputInsertedByTheUser =
                new ByteArrayInputStream((xCoord + System.lineSeparator() + yCoord + System.lineSeparator()).getBytes());

        setFakeInputScannerFromInputStreamOrFailTest(fakeInputInsertedByTheUser);

        assert this.mainViewmodel != null;
        Stream.of(this.mainViewmodel.getCurrentBlackPlayer(), this.mainViewmodel.getCurrentWhitePlayer())
                // TODO: discuss if it is correct that we must manually set the currentGame (only when running the entire class test, if we run only this test it works)
                .filter(player -> player instanceof HumanPlayer)
                .forEach(player -> {
                    try {
                        TestUtility.setFieldValue("currentGame", this.firstGameOfMatch, player);
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        loggerThisClass.log(Level.SEVERE, "Error when forcing to set the game to a player", e);
                    }
                });

        View<?> finalMainView = mainView;
        Thread userMoveExecutor = new Thread(() -> {
            Thread thisThread = Thread.currentThread();
            Thread interrupter = new Thread(() -> {
                while (true) {  // TODO : improve?
                    try {
                        if (!(System.in.available() == 0)) break;
                    } catch (IOException e) {
                        loggerThisClass.log(Level.SEVERE, "Exception in user move executor thread", e);
                    }
                }
                thisThread.interrupt();
            });
            interrupter.setName("MoveExecutorInterrupter");
            interrupter.start();
            try {
                TestUtility.invokeMethodOnObject(finalMainView, "waitForAMoveOfAPlayer");
            } catch (NoSuchFieldException | InvocationTargetException | IllegalAccessException e) {
                fail(e);
            }
            interrupter.interrupt();
        });
        userMoveExecutor.setName("UserMoveExecutor");
        userMoveExecutor.start();
        try {
            userMoveExecutor.join();
        } catch (InterruptedException ignored) {
        }

        assert this.boardOfFirstGame != null;
        assertFalse(Objects.requireNonNull(this.boardOfFirstGame.getCellAtCoordinatesOrNullIfInvalid(new Coordinates(xCoord, yCoord))).isEmpty());
    }

    private void setFakeInputScannerFromInputStreamOrFailTest(@NotNull final InputStream fakeInputInsertedByTheUser) {
        try {
            System.setIn(fakeInputInsertedByTheUser);
            IOUtility.SettableScannerSingleton.createNewScannerForSystemInIfAllowedOrUseTheDefaultAndGet();
            TestUtility.getFieldAlreadyMadeAccessible(IOUtility.SettableScannerSingleton.class, "scannerCanBeModified")
                    .set(null, false);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    private View<?> getCurrentView() throws NoSuchFieldException, IllegalAccessException {
        return (View<?>) TestUtility.getFieldAlreadyMadeAccessible(CLISceneController.class, "currentView").get(null);
    }

    private List<? extends PropertyObserver<?>> getObservedPropertiesByMainViewmodel()
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        assert this.mainViewmodel != null;
        return ((List<?>)
                Objects.requireNonNull(
                        TestUtility.getFieldValue("modelPropertyObservers", this.mainViewmodel)))
                .stream()
                .map(propertyObserver -> (PropertyObserver<?>) propertyObserver)
                .toList();
    }

    private List<? extends PropertyObserver<?>> getPropertiesObservedByView(@NotNull final View<?> view)
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        return ((List<?>)
                Objects.requireNonNull(
                        TestUtility.getFieldValue(
                                "propertiesObservedInViewModel", Objects.requireNonNull(view))))
                .stream()
                .map(propertyObserver -> (PropertyObserver<?>) propertyObserver)
                .toList();
    }


    private void setFieldsInStartViewmodelFromSetup(Setup setup)
            throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        this.startViewmodel = (StartViewmodel)
                ((AtomicReference<?>) Objects.requireNonNull(TestUtility.getFieldValue(
                        "startViewmodelAtomicReference", cliSceneControllerInstanceGetter.get()))).get();
        TestUtility.invokeMethodOnObject(
                this.startViewmodel, "setFieldValuesFromSetupWithoutSetters", setup);
    }

}