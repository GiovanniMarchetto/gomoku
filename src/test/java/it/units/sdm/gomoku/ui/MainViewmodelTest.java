package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static it.units.sdm.gomoku.ui.FakeMainViewmodel.*;
import static org.junit.jupiter.api.Assertions.*;


class MainViewmodelTest {

    private MainViewmodel mainViewmodel;

    @BeforeEach
    void setUp() {
        mainViewmodel = new FakeMainViewmodel();
    }

    @Test
    void tryToTriggerFirstMoveWithoutGameStart() {
        try {
            mainViewmodel.triggerFirstMove();
            fail("The match is not start");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    void beforeTriggerFirstMoveCurrentPlayerIsNull() {
        try {
            mainViewmodel.startNewMatch();
            assertNull(mainViewmodel.getCurrentGame().getCurrentPlayerProperty().getPropertyValue());
        } catch (NullPointerException e) {
            fail(e);
        }
    }

    @Test
    void triggerFirstMove() {
        try {
            mainViewmodel.startNewMatch();
            mainViewmodel.triggerFirstMove();

            Game currentGame = mainViewmodel.getCurrentGame();
            assertEquals(Game.Status.STARTED, currentGame.getGameStatusProperty().getPropertyValue());

            Player currentPlayer = currentGame.getCurrentPlayerProperty().getPropertyValue();
            assertEquals(player1, currentPlayer);

            //noinspection ConstantConditions
            assertEquals(Color.BLACK, currentGame.getColorOfPlayer(currentPlayer));
        } catch (NullPointerException e) {
            fail(e);
        }
    }

    @Test
    void setMatch() {
        try {
            Match match = new Match(setup);
            mainViewmodel.setMatch(match);
            assertEquals(match, TestUtility.getFieldValue("match", mainViewmodel));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }

    }

//    @Test  //TODO: to fix
//    void startNewGame() throws GameAlreadyStartedException {
//        mainViewmodel.createMatchFromSetupAndInitializeNewGame(setup);
//        Game first = mainViewmodel.getCurrentGame();
//        first.start();
//        GameTestUtility.disputeGameAndDraw(first);
//        mainViewmodel.initializeNewGame();
//        assertNotEquals(first, mainViewmodel.getCurrentGame());
//    }

//    @Test
//    void initializeNewGameAfterEndMatch() {
    //TODO: the initializeNewGame must pass the exception?
//        mainViewmodel.startNewMatch();
//        for (int i = 1; i < setup.numberOfGames().intValue(); i++) {
//            mainViewmodel.initializeNewGame();
//        }
//        Game oldGame = mainViewmodel.getCurrentGame();
//        mainViewmodel.initializeNewGame();
//        Game  newGame = mainViewmodel.getCurrentGame();
//        assertEquals(oldGame, newGame);
//    }

//    @Test //TODO: to fix
//    void initializeExtraGame() throws GameAlreadyStartedException {
//        mainViewmodel.startNewMatch();
//        Game oldGame = mainViewmodel.getCurrentGame();
//        oldGame.start();
//        GameTestUtility.disputeGameAndDraw(oldGame);
//
//        for (int i = 1; i < setup.numberOfGames().intValue(); i++) {
//            mainViewmodel.initializeNewGame();
//            oldGame = mainViewmodel.getCurrentGame();
//            oldGame.start();
//            GameTestUtility.disputeGameAndDraw(oldGame);
//            System.out.println(i+" - "+oldGame.isEnded());
//        }
//
//        mainViewmodel.startExtraGame();
//        Game newGame = mainViewmodel.getCurrentGame();
//        assertNotEquals(oldGame, newGame);
//    }

    @Test
    void addAnExtraGameToThisMatch() {
        mainViewmodel.startNewMatch();
        try {
            Match match = (Match) Objects.requireNonNull(TestUtility.getFieldValue("match", mainViewmodel));
            int expected = match.getTotalNumberOfGames() + 1;
            mainViewmodel.addAnExtraGameToThisMatch();
            int actual = match.getTotalNumberOfGames();
            assertEquals(expected, actual);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void checkNullElementsReturnExceptionBeforeCreateTheFirstMatch() {
        //TODO: this is the only test with sense for getters
        // (the others are already tested in the model)
        //if require match must be call setMatch()
        //if require currentGame must be call initializeNewGame
    }

    @Test
    void getCurrentGame() {
        mainViewmodel.startNewMatch();
        try {
            Game game = (Game) TestUtility.getFieldValue("currentGame", mainViewmodel);
            assertEquals(game, mainViewmodel.getCurrentGame());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getCurrentBoard() {
        mainViewmodel.startNewMatch();
        try {
            Board board = (Board) TestUtility.getFieldValue("currentBoard", mainViewmodel);
            assertEquals(board, mainViewmodel.getCurrentBoard());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getCurrentPlayerProperty() {
        mainViewmodel.startNewMatch();
        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Player> currentPlayerProperty =
                    (ObservableProperty<Player>) TestUtility.getFieldValue("currentPlayerProperty", mainViewmodel);
            assertEquals(currentPlayerProperty, mainViewmodel.getCurrentPlayerProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getCurrentGameStatusProperty() {
        mainViewmodel.startNewMatch();
        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Game.Status> currentGameStatusProperty =
                    (ObservableProperty<Game.Status>) TestUtility.getFieldValue("currentGameStatusProperty", mainViewmodel);
            assertEquals(currentGameStatusProperty, mainViewmodel.getCurrentGameStatusProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getUserMustPlaceNewStoneProperty() {
        mainViewmodel.startNewMatch();
        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Boolean> userMustPlaceNewStoneProperty =
                    (ObservableProperty<Boolean>) TestUtility.getFieldValue("userMustPlaceNewStoneProperty", mainViewmodel);
            assertEquals(userMustPlaceNewStoneProperty, mainViewmodel.getUserMustPlaceNewStoneProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getLastMoveCoordinatesProperty() {
        mainViewmodel.startNewMatch();
        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Coordinates> lastMoveCoordinatesProperty =
                    (ObservableProperty<Coordinates>) TestUtility.getFieldValue("lastMoveCoordinatesProperty", mainViewmodel);
            assertEquals(lastMoveCoordinatesProperty, mainViewmodel.getLastMoveCoordinatesProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void placeStoneFromUserWithoutPermission() {
        mainViewmodel.startNewMatch();
        try {
            @SuppressWarnings("unchecked")
            ObservablePropertySettable<Boolean> userMustPlaceNewStoneProperty =
                    (ObservablePropertySettable<Boolean>) TestUtility.getFieldValue("userMustPlaceNewStoneProperty", mainViewmodel);
            assert userMustPlaceNewStoneProperty != null;
            TestUtility.setFieldValue("value", false,
                    Objects.requireNonNull(TestUtility.getFieldValue("propertyValueContainer", userMustPlaceNewStoneProperty)));
            Coordinates coordinates = new Coordinates(0, 0);
            mainViewmodel.placeStoneFromUser(coordinates);
            assertTrue(mainViewmodel.getCellAtCoordinatesInCurrentBoard(coordinates).isEmpty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("setFieldValue Exception");
            fail(e);
        } catch (BoardIsFullException | GameEndedException | CellAlreadyOccupiedException e) {
            System.err.println("placeStoneFromUser Exception");
            fail(e);
        } catch (CellOutOfBoardException e) {
            fail(new IllegalStateException(e));
        }
    }

    @Test
    void placeStoneFromUser()
            throws InterruptedException, BoardIsFullException, GameEndedException,
            CellOutOfBoardException, CellAlreadyOccupiedException, NoSuchFieldException, IllegalAccessException, GameAlreadyStartedException {

        HumanPlayer humanPlayer = new HumanPlayer("Human");
        Setup setupWithHuman = new Setup(
                humanPlayer, player1, numberOfGames, boardSize);
        mainViewmodel.createMatchFromSetupAndInitializeNewGame(setupWithHuman);
        mainViewmodel.getCurrentGame().start();

        final int REASONABLE_TIME_AFTER_WHICH_THREAD_WILL_BE_INTERRUPTED_IN_MILLIS = 10;
        Thread separateThreadWhichWaitForCurrentGameToBeSet = new Thread(() -> {
            while (true) {
                try {
                    if (TestUtility.getFieldValue("currentGame", humanPlayer) != null) {
                        break;
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    fail(e);
                }
            }
        });
        separateThreadWhichWaitForCurrentGameToBeSet.start();
        TestUtility.interruptThreadAfterDelayIfNotAlreadyJoined(    // TODO: re-see this
                separateThreadWhichWaitForCurrentGameToBeSet,
                REASONABLE_TIME_AFTER_WHICH_THREAD_WILL_BE_INTERRUPTED_IN_MILLIS);

        separateThreadWhichWaitForCurrentGameToBeSet.join();

        @SuppressWarnings("unchecked")
        ObservablePropertySettable<Boolean> userMustPlaceNewStoneProperty =
                (ObservablePropertySettable<Boolean>) TestUtility.getFieldValue(
                        "userMustPlaceNewStoneProperty", mainViewmodel);
        assert userMustPlaceNewStoneProperty != null;
        TestUtility.setFieldValue("value", true,
                Objects.requireNonNull(TestUtility.getFieldValue("propertyValueContainer", userMustPlaceNewStoneProperty)));
        Coordinates coordinates = new Coordinates(0, 0);
        mainViewmodel.placeStoneFromUser(coordinates);
        Thread.sleep(100);  // TODO: rethink about the architecture_ here we have to wait for another thread (who knows which one) to update the model: is this correct?
//            assertFalse(mainViewmodel.getCellAtCoordinatesInCurrentBoard(coordinates).isEmpty());//TODO: re-do
    }
}