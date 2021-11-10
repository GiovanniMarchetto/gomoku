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

class AbstractMainViewmodelTest {

    private final AbstractMainViewmodel abstractMainViewmodel = new TestMainViewmodel();

    @Test
    void initializeNewGame() {
        //TODO
    }

    @Test
    void tryToTriggerFirstMoveWithoutGameStart() {
        try {
            abstractMainViewmodel.triggerFirstMove();
            fail("The match is not start");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    void beforeTriggerFirstMoveCurrentPlayerIsNull() {
        try {
            abstractMainViewmodel.startNewMatch();
            assertNull(abstractMainViewmodel.getCurrentGame().getCurrentPlayer().getPropertyValue());
        } catch (NullPointerException e) {
            fail(e);
        }
    }

    @Test
    void triggerFirstMove() {
        try {
            abstractMainViewmodel.startNewMatch();
            abstractMainViewmodel.triggerFirstMove();

            Game currentGame = abstractMainViewmodel.getCurrentGame();
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
            Field matchField = TestUtility.getFieldAlreadyMadeAccessible(AbstractMainViewmodel.class, "match");
            Field gameField = TestUtility.getFieldAlreadyMadeAccessible(AbstractMainViewmodel.class, "currentGame");

            assertNull(matchField.get(abstractMainViewmodel));
            assertNull(gameField.get(abstractMainViewmodel));

            abstractMainViewmodel.createMatchFromSetupAndStartGame(setup);
            assertNotNull(matchField.get(abstractMainViewmodel));
            assertNotNull(gameField.get(abstractMainViewmodel));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void startNewGame() {
        abstractMainViewmodel.createMatchFromSetupAndStartGame(setup);
        Game first = abstractMainViewmodel.getCurrentGame();
        abstractMainViewmodel.startNewGame();
        assertNotEquals(first, abstractMainViewmodel.getCurrentGame());
    }

    @Test
    void startNewGameAfterEndMatch() {
        //TODO: the startNewGame must pass the exception?
    }

    @Test
    void startExtraGame() {
        abstractMainViewmodel.startNewMatch();
        Game oldGame = abstractMainViewmodel.getCurrentGame();
        Game newGame = null;
        while (oldGame != newGame) {
            try {
                System.out.println("game");
                oldGame = abstractMainViewmodel.getCurrentGame();
                abstractMainViewmodel.startNewGame();
                newGame = abstractMainViewmodel.getCurrentGame();
            } catch (Exception e) {
                System.err.println(e);
                break;
            }
        }

        abstractMainViewmodel.startExtraGame();
        newGame = abstractMainViewmodel.getCurrentGame();
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
    void isMatchEndedWithADraft() {
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