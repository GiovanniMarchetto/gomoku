package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
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
            GameTest.disputeGameWithSmartAlgorithm(currentGame);
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
        startGameAndDraft();
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
                startGameAndDraft();
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
            startGameAndDraft();
            assertFalse(match.isEnded());
        }
    }

    @Test
    void isEndedAfterStartLastGame() {
        for (int i = 0; i < NUMBER_OF_GAMES - 1; i++) {
            startGameAndDraft();
        }
        startNewGameComplete();
        assertFalse(match.isEnded());
    }

    @Test
    void isEndedNormalFlow() {
        isEndedAfterStartLastGame();
        GameTest.disputeGameWithSmartAlgorithm(currentGame);
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
        startGameAndDraft();
        assertTrue(match.isEnded());
    }

    @Test
    void isADraftMatchNotEnded() {
        try {
            match.isADraft();
            fail("Not throw MatchNotEndedException");
        } catch (Match.MatchNotEndedException ignored) {
        }
    }

    @Test
    void isADraft() {
        for (int i = 0; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraft();
        }
        try {
            assertTrue(match.isADraft());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

    @Test
    void isNotADraft() {
        startGameAndPlayerWin(cpu1);
        for (int i = 1; i < NUMBER_OF_GAMES; i++) {
            startGameAndDraft();
        }
        try {
            assertFalse(match.isADraft());
        } catch (Match.MatchNotEndedException e) {
            fail(e);
        }
    }

    //region Support methods
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

        try {
            for (int i = 0; i < 4; i++) {
                currentGame.placeStoneAndChangeTurn(new Coordinates(i, 0));
                currentGame.placeStoneAndChangeTurn(new Coordinates(i, 1));
            }

            if (player == currentGame.getCurrentPlayer().getPropertyValue()) {
                currentGame.placeStoneAndChangeTurn(new Coordinates(4, 0));
            } else {
                currentGame.placeStoneAndChangeTurn(new Coordinates(0, 2));
                currentGame.placeStoneAndChangeTurn(new Coordinates(4, 1));
            }

            if (currentGame.getWinner() != player) {
                fail("The winner is not the correct player");
            }
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameNotEndedException | Game.GameEndedException e) {
            fail(e);
        }
    }

    private void startGameAndDraft() {
        startNewGameComplete();
        for (int x = 0; x < boardSizeTest; x++) {
            for (int y = 0; y < boardSizeTest; y++) {
                if (x % 3 == 0 && y == 0) {
                    try {
                        currentGame.placeStoneAndChangeTurn(new Coordinates(x, y));
                    } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameEndedException e) {
                        fail(e);
                    }
                }
            }
        }

        while (!currentGame.isEnded()) {
            try {
                currentGame.placeStoneAndChangeTurn(cpu1.chooseNextEmptyCoordinates(currentGame.getBoard()));
            } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameEndedException e) {
                fail(e);
            }
        }

        try {
            if (currentGame.getWinner() != null) {
                fail("NOT A DRAFT");
            }
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    //endregion

}