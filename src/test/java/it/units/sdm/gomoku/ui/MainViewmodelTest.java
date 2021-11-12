package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.ui.support.Setup;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static it.units.sdm.gomoku.ui.TestMainViewmodel.*;
import static org.junit.jupiter.api.Assertions.*;

class MainViewmodelTest {

    private MainViewmodel mainViewmodel;

    @BeforeEach
    void setUp() {
        mainViewmodel = new TestMainViewmodel();
    }

    @Test
    void initializeNewGame() {
        //TODO: see if inline
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
            assertNull(mainViewmodel.getCurrentGame().getCurrentPlayer().getPropertyValue());
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
            assertEquals(Game.Status.STARTED, currentGame.getGameStatus().getPropertyValue());

            Player currentPlayer = currentGame.getCurrentPlayer().getPropertyValue();
            assertEquals(cpuPlayer1, currentPlayer);

            //noinspection ConstantConditions
            assertEquals(Stone.Color.BLACK, currentGame.getColorOfPlayer(currentPlayer));
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

    @Test
    void startNewGame() {
        mainViewmodel.createMatchFromSetupAndStartGame(setup);
        Game first = mainViewmodel.getCurrentGame();
        mainViewmodel.startNewGame();
        assertNotEquals(first, mainViewmodel.getCurrentGame());
    }

    @Test
    void startNewGameAfterEndMatch() {
        //TODO: the startNewGame must pass the exception?
//        mainViewmodel.startNewMatch();
//        for (int i = 1; i < setup.numberOfGames().intValue(); i++) {
//            mainViewmodel.startNewGame();
//        }
//        Game oldGame = mainViewmodel.getCurrentGame();
//        mainViewmodel.startNewGame();
//        Game  newGame = mainViewmodel.getCurrentGame();
//        assertEquals(oldGame, newGame);
    }

    @Test
    void startExtraGame() {
        mainViewmodel.startNewMatch();
        for (int i = 1; i < setup.numberOfGames().intValue(); i++) {
            mainViewmodel.startNewGame();
        }
        Game oldGame = mainViewmodel.getCurrentGame();
        mainViewmodel.startExtraGame();
        Game newGame = mainViewmodel.getCurrentGame();
        assertNotEquals(oldGame, newGame);
    }

    @Test
    void addAnExtraGameToThisMatch() {
        mainViewmodel.startNewMatch();
        try {
            Match match = (Match) Objects.requireNonNull(TestUtility.getFieldValue("match", mainViewmodel));
            int expected = match.getNumberOfGames() + 1;
            mainViewmodel.addAnExtraGameToThisMatch();
            int actual = match.getNumberOfGames();
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
        //if require currentGame must be call startNewGame/initialize Game
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
            ObservableProperty<Boolean> userMustPlaceNewStoneProperty =
                    (ObservableProperty<Boolean>) TestUtility.getFieldValue("userMustPlaceNewStoneProperty", mainViewmodel);
            //noinspection ConstantConditions
            userMustPlaceNewStoneProperty.setPropertyValueWithoutNotifying(false);
            Coordinates coordinates = new Coordinates(0, 0);
            mainViewmodel.placeStoneFromUser(coordinates);
            assertTrue(mainViewmodel.getCellAtCoordinatesInCurrentBoard(coordinates).isEmpty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.err.println("setFieldValue Exception");
            fail(e);
        } catch (Board.BoardIsFullException | Game.GameEndedException | Board.CellAlreadyOccupiedException e) {
            System.err.println("placeStoneFromUser Exception");
            fail(e);
        }
    }

    @Test
    void placeStoneFromUser() {
        HumanPlayer humanPlayer = new HumanPlayer("Human");
        Setup setupWithHuman = new Setup(
                humanPlayer, cpuPlayer1, numberOfGames, boardSize);
        mainViewmodel.createMatchFromSetupAndStartGame(setupWithHuman);
        mainViewmodel.getCurrentGame().start();

        while (true) {
            try {
                //give the time to fire and set the currentGame
                if (TestUtility.getFieldValue("currentGame", humanPlayer) != null) {
                    break;
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
            }
        }

        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Boolean> userMustPlaceNewStoneProperty =
                    (ObservableProperty<Boolean>) TestUtility.getFieldValue(
                            "userMustPlaceNewStoneProperty", mainViewmodel);
            //noinspection ConstantConditions
            userMustPlaceNewStoneProperty.setPropertyValueWithoutNotifying(true);
            Coordinates coordinates = new Coordinates(0, 0);
            mainViewmodel.placeStoneFromUser(coordinates);
            assertFalse(mainViewmodel.getCellAtCoordinatesInCurrentBoard(coordinates).isEmpty());
        } catch (NoSuchFieldException | IllegalAccessException
                | Board.BoardIsFullException | Game.GameEndedException
                | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }
}