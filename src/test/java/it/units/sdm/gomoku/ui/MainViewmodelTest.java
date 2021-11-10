package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

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
    void createMatchFromSetupAndStartGame() {
        try {
            Field matchField = TestUtility.getFieldAlreadyMadeAccessible(MainViewmodel.class, "match");
            Field gameField = TestUtility.getFieldAlreadyMadeAccessible(MainViewmodel.class, "currentGame");

            assertNull(matchField.get(mainViewmodel));
            assertNull(gameField.get(mainViewmodel));

            mainViewmodel.createMatchFromSetupAndStartGame(setup);
            assertNotNull(matchField.get(mainViewmodel));
            assertNotNull(gameField.get(mainViewmodel));
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
    }

    @Test
    void startExtraGame() {
        mainViewmodel.startNewMatch();
        Game oldGame = mainViewmodel.getCurrentGame();
        Game newGame = null;
        while (oldGame != newGame) {
            try {
                System.out.println("game");
                oldGame = mainViewmodel.getCurrentGame();
                mainViewmodel.startNewGame();
                newGame = mainViewmodel.getCurrentGame();
            } catch (Exception e) {
                System.err.println(e);
                break;
            }
        }

        mainViewmodel.startExtraGame();
        newGame = mainViewmodel.getCurrentGame();
        assertNotEquals(oldGame, newGame);
    }

    @Test
    void addAnExtraGameToThisMatch() {
        //TODO
    }

    @Test
    void isMatchEnded() {
        //TODO
    }

    @Test
    void isMatchEndedWithADraw() {
        //TODO
    }

    @Test
    void isCurrentGameEnded() {
        //TODO
    }

    @Test
    void setMatch() {
        //TODO
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