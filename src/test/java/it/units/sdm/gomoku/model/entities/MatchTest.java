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
            disputeGame(currentGame);
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
    void getScorePlayer1Win() {
        startAndDisputeNewGameAndEndEithWinOfBlackPlayer();
        assertCpusScore(1, 0);
    }

    @Test
    void getScoreTwoPlays() {
        getScorePlayer1Win();

        startAndDisputeNewGameAndEndEithWinOfBlackPlayer();
        assertCpusScore(1, 1);
    }

    @Test
    void getScoreWithDraw() {
        startAndDisputeNewGameAndEndWithDraft();
        assertCpusScore(0, 0);
    }


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

    private void disputeGame(Game game) {
        CPUPlayer cpuPlayer = new CPUPlayer();
        while (!game.isEnded()) {
            try {
                game.placeStoneAndChangeTurn(cpuPlayer.chooseSmartEmptyCoordinates(game.getBoard()));
            } catch (Board.CellAlreadyOccupiedException | Board.BoardIsFullException e) {
                fail(e);
            }
        }
    }

    private void startAndDisputeNewGameAndEndEithWinOfBlackPlayer() {
        startNewGameComplete();

        try {
            for (int i = 0; i < 4; i++) {
                currentGame.placeStoneAndChangeTurn(new Coordinates(i, 0));
                currentGame.placeStoneAndChangeTurn(new Coordinates(i, 1));
            }
            currentGame.placeStoneAndChangeTurn(new Coordinates(4, 0));

            if (currentGame.getWinner() != match.getCurrentBlackPlayer()) {
                fail("The winner is not the black palyer");
            }
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameNotEndedException e) {
            fail(e);
        }
    }

    private void startAndDisputeNewGameAndEndWithDraft() {
        startNewGameComplete();
        for (int x = 0; x < boardSizeTest; x++) {
            for (int y = 0; y < boardSizeTest; y++) {
                if (x % 3 == 0 && y == 0) {
                    try {
                        currentGame.placeStoneAndChangeTurn(new Coordinates(x, y));
                    } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
                        fail(e);
                    }
                }
            }
        }

        while (!currentGame.isEnded()) {
            try {
                currentGame.placeStoneAndChangeTurn(cpu1.chooseNextEmptyCoordinates(currentGame.getBoard()));
            } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
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


}