package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.game.GameTestUtility;
import it.units.sdm.gomoku.model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchTest {

    private final CPUPlayer cpu1 = new CPUPlayer();
    private final CPUPlayer cpu2 = new CPUPlayer();
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
            currentGame = match.initializeNewGame();
            currentGame.start();
        } catch (MatchEndedException | GameNotEndedException | GameAlreadyStartedException e) {
            fail(e);
        }
    }

    private void startGameAndPlayerWin(Player player) {
        startNewGameComplete();
        GameTestUtility.disputeGameAndMakeThePlayerToWin(currentGame, player);
    }

    private void startGameAndDraw() {
        startNewGameComplete();
        GameTestUtility.disputeGameAndDraw(currentGame);
    }
    //endregion Support Methods

    @BeforeEach
    void setup() {
        final PositiveInteger boardSizeTest = new PositiveInteger(5);
        match = new Match(boardSizeTest, new PositiveInteger(NUMBER_OF_GAMES), cpu1, cpu2);
    }

    @Test
    void addFirstGameOfTheMatchToGameList() throws MatchEndedException, NoSuchFieldException, IllegalAccessException, GameNotEndedException {
        currentGame = match.initializeNewGame();
        Field fieldGameList = match.getClass().getDeclaredField("gameList");
        fieldGameList.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Game> gameList = (List<Game>) fieldGameList.get(match);
        assertEquals(currentGame, gameList.get(0));
    }

    @Test
    void setFirstPlayerAsTheBlackOneInTheFirstGame() throws MatchEndedException, GameNotEndedException {
        match.initializeNewGame();
        assertEquals(cpu1, match.getCurrentBlackPlayer());
    }

    @Test
    void setFirstPlayerAsTheWhiteOneInTheSecondGame()
            throws MatchEndedException, GameNotEndedException, GameAlreadyStartedException {

        currentGame = match.initializeNewGame();
        currentGame.start();
        GameTestUtility.disputeGameAndDraw(currentGame);
        match.initializeNewGame();
        assertEquals(cpu1, match.getCurrentWhitePlayer());
    }

//    @Test
//    void maxNumberOfGamesException() {//TODO: substute with not game ended exception
//        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
//            startNewGameComplete();
//            GameTestUtility.disputeGameWithSmartAlgorithm(currentGame);
//        }
//
//        try {
//            match.initializeNewGame();
//            fail("Is over the number of games!");
//        } catch (MatchEndedException | GameNotEndedException e) {
//            fail(e);
//        }
//    }

    @Test
    void getScoreBeforeStart() {
        assertCpusScore(0, 0);
    }

    @Test
    void getScoreAfterStart() throws MatchEndedException, GameNotEndedException {
        match.initializeNewGame();
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
        } catch (MatchNotEndedException ignored) {
        }
    }

    @Test
    void getWinnerWithADraw() throws MatchNotEndedException {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        assertNull(match.getWinner());
    }

    @Test
    void getWinnerWithCPU1Win() throws MatchNotEndedException {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startGameAndPlayerWin(cpu1);
        }
        assertEquals(cpu1, match.getWinner());
    }

    @Test
    void getWinnerWithCPU2Win() throws MatchNotEndedException {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startGameAndPlayerWin(cpu2);
        }
        assertEquals(cpu2, match.getWinner());
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
    void isEndedNormalFlow() throws GameNotStartedException {
        isEndedAfterStartLastGame();
        GameTestUtility.disputeGameWithSmartAlgorithm(currentGame);
        assertTrue(match.isEnded());
    }

    @Test
    void isEndedAfterAddExtraGame() throws GameNotStartedException {
        isEndedNormalFlow();
        match.incrementTotalNumberOfGames();
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedAfterEndExtraGame() throws GameNotStartedException {
        isEndedAfterAddExtraGame();
        startGameAndDraw();
        assertTrue(match.isEnded());
    }

    @Test
    void isADrawMatchNotEnded() {
        try {
            match.isADraw();
            fail("Not throw MatchNotEndedException");
        } catch (MatchNotEndedException ignored) {
        }
    }

    @Test
    void isADraw() {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        try {
            assertTrue(match.isADraw());
        } catch (MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void isNotADraw() throws MatchNotEndedException {
        startGameAndPlayerWin(cpu1);
        for (int i = 1; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraw();
        }
        assertFalse(match.isADraw());
    }

}