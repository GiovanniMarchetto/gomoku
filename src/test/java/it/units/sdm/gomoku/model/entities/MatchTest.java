package it.units.sdm.gomoku.model.entities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchTest {

    private final int boardSizeTest = 19;
    CPUPlayer cpu1 = new CPUPlayer("First Captain Gomoku");
    CPUPlayer cpu2 = new CPUPlayer("Second Iron Keroro");
    Match match;
    Game currentGame;

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

            Game gameProvided = match.startNewGame();
            assertEquals(gameList.get(0), gameProvided);

            Field fieldBlackPlayer = match.getClass().getDeclaredField("currentBlackPlayer");
            fieldBlackPlayer.setAccessible(true);
            Player currentBlackPlayer = (Player) fieldBlackPlayer.get(match);

            assertEquals(cpu1, currentBlackPlayer);

            Field fieldWhitePlayer = match.getClass().getDeclaredField("currentWhitePlayer");
            fieldWhitePlayer.setAccessible(true);
            Player currentWhitePlayer = (Player) fieldWhitePlayer.get(match);
            assertEquals(cpu2, currentWhitePlayer);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getScoreBeforeStart() {
        assertCpusScore(0, 0);
    }

    @Test
    void getScoreAfterStart() {
        match.startNewGame();
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
        currentGame = match.startNewGame();
        for (int i = 0; i < 5; i++) {
            cpuPlaceStoneNextEmptyCoordinates(cpuPlayer);
        }
        assertCpusScore(score1, score2);
    }

    @Test
    void getScoreWithDraw() {
        currentGame = match.startNewGame();
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
            currentGame.placeStone(cpuPlayer, cpu1.chooseNextEmptyCoordinates(currentGame.getBoard()));
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            e.printStackTrace();
        }
    }

    private void assertCpusScore(int n1, int n2) {
        assertEquals(n1, match.getScore().get(cpu1).intValue());
        assertEquals(n2, match.getScore().get(cpu2).intValue());
    }

}