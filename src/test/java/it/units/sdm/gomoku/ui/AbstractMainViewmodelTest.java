package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static it.units.sdm.gomoku.ui.TestMainViewmodel.cpuPlayer1;
import static it.units.sdm.gomoku.ui.TestMainViewmodel.setup;
import static org.junit.jupiter.api.Assertions.*;

class AbstractMainViewmodelTest {

    private final Match match = new Match(setup);
    private final AbstractMainViewmodel abstractMainViewmodel = new TestMainViewmodel();

    private Player currentPlayerProperty;
    private Game.Status currentGameStatusProperty;
    private boolean userMustPlaceNewStoneProperty;
    private Coordinates lastMoveCoordinatesProperty;
    private Game currentGame;
    private Board currentBoard;

    @BeforeEach
    void setup() {
//        System.out.println(abstractMainViewmodel);
    }

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
            currentGame = abstractMainViewmodel.getCurrentGame();
            assertNull(currentGame.getCurrentPlayer().getPropertyValue());
        } catch (NullPointerException e) {
            fail(e);
        }
    }

    @Test
    void triggerFirstMove() {
        try {
            abstractMainViewmodel.startNewMatch();
            abstractMainViewmodel.triggerFirstMove();

            currentGame = abstractMainViewmodel.getCurrentGame();
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
            assertNull(matchField.get(abstractMainViewmodel));
            Field gameField = TestUtility.getFieldAlreadyMadeAccessible(AbstractMainViewmodel.class, "currentGame");
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
    void startExtraGame() {
        //TODO
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