package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.TestUtility.readBoardsWithWinCoordsAndResultsFromCSV;
import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private final CPUPlayer cpuBlack = new CPUPlayer("cpuBlack");
    private final CPUPlayer cpuWhite = new CPUPlayer("cpuWhite");
    private Game game;

    private static Stream<Arguments> readBoardsWithWinCoordsAndResultsFromSampleCSV() {
        return readBoardsWithWinCoordsAndResultsFromCSV(EnvVariables.END_GAMES);
    }

    private void setGameFromCsv(Game voidGame, Stone[][] boardStone) {
        for (int x = 0; x < boardStone.length; x++) {
            for (int y = 0; y < boardStone.length; y++) {
                if (!boardStone[x][y].isNone()) {
                    Player playerFoundInBoard;
                    if (boardStone[x][y] == Stone.BLACK) {
                        playerFoundInBoard = cpuBlack;
                    } else {
                        playerFoundInBoard = cpuWhite;
                    }
                    try {
                        Method placeStoneMethod = voidGame.getClass().getDeclaredMethod("placeStone", Player.class, Coordinates.class);
                        placeStoneMethod.setAccessible(true);
                        placeStoneMethod.invoke(voidGame, playerFoundInBoard, new Coordinates(x, y));
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @ParameterizedTest
    @MethodSource("readBoardsWithWinCoordsAndResultsFromSampleCSV")
    void placeStone(Stone[][] matrix, Coordinates coordinates, boolean finishedGame) throws NoSuchFieldException, IllegalAccessException {
        game = new Game(matrix.length, cpuBlack, cpuWhite);
        setGameFromCsv(game, matrix);

        Field fieldBoardGame = game.getClass().getDeclaredField("board");
        fieldBoardGame.setAccessible(true);
        Board expectedBoard = TestUtility.createBoardFromBoardStone(matrix, matrix.length);

        assertEquals(expectedBoard, fieldBoardGame.get(game));

        if (finishedGame) {
            try {
                if (expectedBoard.isAnyEmptyPositionOnTheBoard()) {
                    assertNotNull(game.getWinner());
                } else {
                    assertNull(game.getWinner());
                }
            } catch (Game.GameNotEndedException e) {
                e.printStackTrace();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("readBoardsWithWinCoordsAndResultsFromSampleCSV")
    void getWinner(Stone[][] matrix, Coordinates coordinates, boolean finishedGame) {
        game = new Game(matrix.length, cpuBlack, cpuWhite);
        setGameFromCsv(game, matrix);
        try {
            Player winner = game.getWinner();
            if (game.getBoard().isAnyEmptyPositionOnTheBoard()) {
                assertNotNull(winner);
            } else {
                assertNull(winner);
            }
        } catch (Game.GameNotEndedException e) {
            assertFalse(finishedGame);
        }
    }

    @ParameterizedTest
    @MethodSource("readBoardsWithWinCoordsAndResultsFromSampleCSV")
    void isThisGameEnded(Stone[][] matrix, Coordinates ignoredC, boolean ignoredB, boolean finishedGame) {
        game = new Game(matrix.length, cpuBlack, cpuWhite);
        setGameFromCsv(game, matrix);
        assertEquals(finishedGame, game.isThisGameEnded());
    }

    @Test
    void compareTo() throws InterruptedException {
        game = new Game(5, cpuBlack, cpuWhite);
        Thread.sleep(1);
        Game otherGame = new Game(5, cpuBlack, cpuWhite);
        assertTrue(game.compareTo(otherGame) < 0);
    }
}