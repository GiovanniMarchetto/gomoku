package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static it.units.sdm.gomoku.ui.TestMainViewmodel.cpuPlayer1;
import static it.units.sdm.gomoku.ui.TestMainViewmodel.setup;
import static org.junit.jupiter.api.Assertions.*;

class MainViewmodelTest {

    private final MainViewmodel mainViewmodel = new TestMainViewmodel();

    @Test
    void initializeNewGame() {
        //TODO
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
            assertEquals(match, TestUtility.getFieldValue("match", match));
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
    void getCurrentBoardAsString() {
        //TODO
    }

    @Test
    void endGame() {
        //TODO
    }

    @Test
    void getCurrentGame() {
        //TODO
    }

    @Test
    void getCurrentBoard() {
        //TODO
    }

    @Test
    void placeStoneFromUser() {
        //TODO
    }

    @Test
    void forceReFireAllCells() {
        //TODO
    }

    @Test
    void getBoardSize() {
        //TODO
    }

    @Test
    void getScoreOfMatch() {
        //TODO
    }

    @Test
    void getCurrentPlayer() {
        //TODO
    }

    @Test
    void getCurrentPlayerProperty() {
        //TODO
    }

    @Test
    void getCurrentGameStatusProperty() {
        //TODO
    }

    @Test
    void getUserMustPlaceNewStoneProperty() {
        //TODO
    }

    @Test
    void getLastMoveCoordinatesProperty() {
        //TODO
    }

    @Test
    void getColorOfCurrentPlayer() {
        //TODO
    }

    @Test
    void getCurrentBlackPlayer() {
        //TODO
    }

    @Test
    void getCurrentWhitePlayer() {
        //TODO
    }

    @Test
    void getCellAtCoordinatesInCurrentBoard() {
        //TODO
    }

    @Test
    void getWinnerOfTheMatch() {
        //TODO
    }

    @Test
    void getWinnerOfTheGame() {
        //TODO
    }

    @Test
    void getGameStartTime() {
        //TODO
    }


}