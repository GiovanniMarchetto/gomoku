package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.custom_types.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchTest {

    CPUPlayer cpu1 = new CPUPlayer("First Captain Gomoku");
    CPUPlayer cpu2 = new CPUPlayer("Second Iron Keroro");
    Match match;

    @BeforeEach
    void setup() {
        match = new Match(cpu1, cpu2, 5, 3);
    }

    @Test
    void startNewGame() {
        try {
            Field fieldGameList = match.getClass().getDeclaredField("gameList");
            fieldGameList.setAccessible(true);
            Object gameListFromField = fieldGameList.get(match);
            assert gameListFromField instanceof List<?>;
            assert ((List<?>) gameListFromField).stream().filter(aGame -> aGame instanceof Game).count()==0L;
            @SuppressWarnings("unchecked")  // casting just checked elementwise
            List<Game> gameList = (List<Game>)gameListFromField;
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
    void getScore() {
        assertCpusScore(0, 0);

        Game currentGame = match.startNewGame();
        assertCpusScore(0, 0);

        disputeGameTest(cpu1, 1, 0);

        disputeGameTest(cpu2, 1, 1);

        disputeGameTest(null, 1, 1);

    }

    private void disputeGameTest(CPUPlayer cpuPlayer, int score1, int score2) {
        Game currentGame = match.startNewGame();
        Board board = currentGame.getBoard();
        try {
            if (cpuPlayer != null) {
                for (int i = 0; i < 5; i++) {
                    currentGame.placeStone(cpuPlayer, cpuPlayer.chooseNextEmptyCoordinates(board));
                }
            } else {
                currentGame.placeStone(cpu1, new Coordinates(0, 0));
                currentGame.placeStone(cpu1, new Coordinates(1, 1));
                currentGame.placeStone(cpu1, new Coordinates(2, 2));
                currentGame.placeStone(cpu1, new Coordinates(3, 3));
                currentGame.placeStone(cpu1, new Coordinates(3, 4));
                currentGame.placeStone(cpu1, new Coordinates(4, 3));
                for (int i = 0; i < ((int) Math.pow(board.getSize(), 2)) - 6; i++) {
                    currentGame.placeStone(cpu2, cpu2.chooseNextEmptyCoordinates(currentGame.getBoard()));
                }
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            e.printStackTrace();
        }

        assertCpusScore(score1, score2);
    }

    private void assertCpusScore(int n1, int n2) {
        assertEquals(n1, match.getScore().get(cpu1).intValue());
        assertEquals(n2, match.getScore().get(cpu2).intValue());
    }

}