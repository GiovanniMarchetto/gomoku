package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private final int boardSizeTest = 19;
    private final CPUPlayer cpu1 = new CPUPlayer("First Captain Gomoku");
    private final CPUPlayer cpu2 = new CPUPlayer("Second Iron Keroro");
    private Match match;
    private Game currentGame;

    @BeforeEach
    void setup() {
        match = new Match(cpu1, cpu2, boardSizeTest, 3);
    }

    @Test
    void startNewGame() {
        try {
            Field fieldGameList = match.getClass().getDeclaredField("gameList");
            fieldGameList.setAccessible(true);
            Object gameListFromField = fieldGameList.get(match);
            assert gameListFromField instanceof List<?>;
            assert ((List<?>) gameListFromField).stream().filter(aGame -> aGame instanceof Game).count() == 0L;
            @SuppressWarnings("unchecked")  // casting just checked elementwise
            List<Game> gameList = (List<Game>) gameListFromField;
            assertTrue(gameList.isEmpty());

            currentGame = match.startNewGame();
            assertEquals(gameList.get(0), currentGame);

            Field fieldBlackPlayer = match.getClass().getDeclaredField("currentBlackPlayer");
            fieldBlackPlayer.setAccessible(true);
            Player currentBlackPlayer = (Player) fieldBlackPlayer.get(match);

            assertEquals(cpu1, currentBlackPlayer);

            Field fieldWhitePlayer = match.getClass().getDeclaredField("currentWhitePlayer");
            fieldWhitePlayer.setAccessible(true);
            Player currentWhitePlayer = (Player) fieldWhitePlayer.get(match);
            assertEquals(cpu2, currentWhitePlayer);

        } catch (NoSuchFieldException | IllegalAccessException | Match.MatchEndedException e) {
            e.printStackTrace();
        }
    }

    @Test
    void matchEndedException() {
        //TODO: start a new game when match is finished
    }

    @Test
    void getScoreBeforeStart() {
        assertCpusScore(0, 0);
    }

    @Test
    void getScoreAfterStart() {
        try {
            match.startNewGame();
        } catch (Match.MatchEndedException e) {
            fail(e);
        }
        assertCpusScore(0, 0);
    }

    @Test
    void getScorePlayer1Win() {
        playerWinGameAndAssertFinalScore(cpu1, 1, 0);
    }

    @Test
    void getScorePlayer2Win() {
        playerWinGameAndAssertFinalScore(cpu2, 0, 1);
    }

    @Test
    void getScoreTwoPlays() {
        getScorePlayer1Win();
        playerWinGameAndAssertFinalScore(cpu2, 1, 1);
    }

    private void playerWinGameAndAssertFinalScore(CPUPlayer cpuPlayer, int score1, int score2) {
        try {
            currentGame = match.startNewGame();
        } catch (Match.MatchEndedException e) {
            fail(e);
        }
        for (int i = 0; i < 5; i++) {
            cpuPlaceStoneNextEmptyCoordinates(cpuPlayer);
        }
        assertCpusScore(score1, score2);
    }

    @Test
    void getScoreWithDraw() {
        try {
            currentGame = match.startNewGame();
        } catch (Match.MatchEndedException e) {
            fail(e);
        }
        CPUPlayer cpuPlayer;
        int rowRest, colRest;

        for (int row = 0; row < boardSizeTest; row++) {
            rowRest = row % 4;
            for (int col = 0; col < boardSizeTest; col++) {
                colRest = col % 2;
                if (rowRest == 0 || rowRest == 1) {
                    cpuPlayer = colRest == 0 ? cpu1 : cpu2;
                } else {
                    cpuPlayer = colRest == 0 ? cpu2 : cpu1;
                }
                cpuPlaceStoneNextEmptyCoordinates(cpuPlayer);
            }
        }

        assertCpusScore(0, 0);
    }

    private void cpuPlaceStoneNextEmptyCoordinates(CPUPlayer cpuPlayer) {
        try {
            Method placeStoneMethod = currentGame.getClass().getDeclaredMethod("placeStone",Player.class, Coordinates.class);
            placeStoneMethod.setAccessible(true);
            placeStoneMethod.invoke(currentGame,cpuPlayer, cpu1.chooseNextEmptyCoordinates(currentGame.getBoard()));
        } catch (Board.NoMoreEmptyPositionAvailableException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void assertCpusScore(int n1, int n2) {
        assertEquals(n1, match.getScore().get(cpu1).intValue());
        assertEquals(n2, match.getScore().get(cpu2).intValue());
    }

}