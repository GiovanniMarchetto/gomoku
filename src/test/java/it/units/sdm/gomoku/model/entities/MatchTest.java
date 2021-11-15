package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.actors.CPUPlayer;
import it.units.sdm.gomoku.model.actors.Player;
import it.units.sdm.gomoku.model.entities.game.GameTestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private final int boardSizeTest = 5;
    private final CPUPlayer cpu1 = new CPUPlayer("First Captain Gomoku");
    private final CPUPlayer cpu2 = new CPUPlayer("Second Iron Keroro");
    private final int NUMBER_OF_GAMES = 3;
    private Match match;
    private Game currentGame;

    //region Support Methods
    private void assertCpusScore(int n1, int n2) {
        assertEquals(n1, match.getScore().get(cpu1).intValue());
        assertEquals(n2, match.getScore().get(cpu2).intValue());
    }

    private void startNewGameComplete() {
        try {
            currentGame = match.startNewGame();
            currentGame.start();
        } catch (Match.MatchEndedException | Match.MaxNumberOfGamesException e) {
            fail(e);
        }
    }

    private void startGameAndPlayerWin(Player player) {
        startNewGameComplete();
        GameTestUtility.disputeGameAndPlayerWin(currentGame, player);
    }

    private void startGameAndDraw() {
        startNewGameComplete();
        GameTestUtility.disputeGameAndDraw(currentGame, boardSizeTest);
    }
    //endregion Support Methods

    @BeforeEach
    void setup() {
        match = new Match(boardSizeTest, NUMBER_OF_GAMES, cpu1, cpu2);
    }

    @Test
    void startNewGame() {
        try {
            Field fieldGameList = match.getClass().getDeclaredField("gameList");
            fieldGameList.setAccessible(true);
            @SuppressWarnings("unchecked")
            List<Game> gameList = (List<Game>) fieldGameList.get(match);

            assertTrue(gameList.isEmpty());

            currentGame = match.startNewGame();
            assertEquals(gameList.get(0), currentGame);

            assertEquals(cpu1, match.getCurrentBlackPlayer());
            assertEquals(cpu2, match.getCurrentWhitePlayer());
        } catch (NoSuchFieldException | IllegalAccessException | Match.MatchEndedException | Match.MaxNumberOfGamesException e) {
            fail(e);
        }
    }

    @Test
    void maxNumberOfGamesException() {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startNewGameComplete();
            GameTestUtility.disputeGameWithSmartAlgorithm(currentGame);
        }

        try {
            match.startNewGame();
            fail("Is over the number of games!");
        } catch (Match.MaxNumberOfGamesException ignored) {
        } catch (Match.MatchEndedException e) {
            fail(e);
        }
    }

    @Test
    void getScoreBeforeStart() {
        assertCpusScore(0, 0);
    }

    @Test
    void getScoreAfterStart() {
        try {
            match.startNewGame();
        } catch (Match.MatchEndedException | Match.MaxNumberOfGamesException e) {
            fail(e);
        }
        assertCpusScore(0, 0);
    }

    @Test
    void getScoreAfterPlayer1Win() {
        startGameAndPlayerWin(cpu1);
        assertCpusScore(1, 0);
    }

    @Test
    void getScoreAfterPlayer2Win() {
        startGameAndPlayerWin(cpu2);
        assertCpusScore(0, 1);
    }

    @Test
    void getScoreAfterADrawGame() {
        startGameAndDraw();
        assertCpusScore(0, 0);
    }

    @Test
    void getWinnerIfMatchNotEnded() {
        try {
            match.getWinner();
            fail("Match not ended");
        } catch (Match.MatchNotEndedException ignored) {
        }
    }

    @Test
    void getWinnerWithADraw() {
        try {
            for (int i = 0; i < NUMBER_OF_GAMES; i++) {
                startGameAndDraw();
            }
            assertNull(match.getWinner());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getWinnerWithCPU1Win() {
        try {
            for (int i = 0; i < NUMBER_OF_GAMES; i++) {
                startGameAndPlayerWin(cpu1);
            }
            assertEquals(cpu1, match.getWinner());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getWinnerWithCPU2Win() {
        try {
            for (int i = 0; i < NUMBER_OF_GAMES; i++) {
                startGameAndPlayerWin(cpu2);
            }
            assertEquals(cpu2, match.getWinner());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void getNumberOfGames() {
        assertEquals(NUMBER_OF_GAMES, match.getNumberOfGames());
    }

    @Test
    void getCurrentBlackPlayer() {
        assertEquals(cpu1, match.getCurrentBlackPlayer());
    }

    @Test
    void getCurrentWhitePlayer() {
        assertEquals(cpu2, match.getCurrentWhitePlayer());
    }

    @Test
    void isEndedAtStartMatch() {
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedAfterAGame() {
        //noinspection ConstantConditions
        if (NUMBER_OF_GAMES != 1) {
            startGameAndDraw();
            assertFalse(match.isEnded());
        }
    }

    @Test
    void isEndedAfterStartLastGame() {
        for (int i = 0; i < NUMBER_OF_GAMES - 1; i++) {
            startGameAndDraw();
        }
        startNewGameComplete();
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedNormalFlow() {
        isEndedAfterStartLastGame();
        GameTestUtility.disputeGameWithSmartAlgorithm(currentGame);
        assertTrue(match.isEnded());
    }

    @Test
    void isEndedAfterAddExtraGame() {
        isEndedNormalFlow();
        match.addAnExtraGame();
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedAfterEndExtraGame() {
        isEndedAfterAddExtraGame();
        startGameAndDraw();
        assertTrue(match.isEnded());
    }

    @Test
    void isADrawMatchNotEnded() {
        try {
            match.isADraw();
            fail("Not throw MatchNotEndedException");
        } catch (Match.MatchNotEndedException ignored) {
        }
    }

    @Test
    void isADraw() {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        try {
            assertTrue(match.isADraw());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void isNotADraw() {
        startGameAndPlayerWin(cpu1);
        for (int i = 1; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        try {
            assertFalse(match.isADraw());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

}