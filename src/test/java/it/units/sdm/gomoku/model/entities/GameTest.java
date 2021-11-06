package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {
    private final CPUPlayer cpuBlack = new CPUPlayer("cpuBlack");
    private final CPUPlayer cpuWhite = new CPUPlayer("cpuWhite");
    private Game game;

    private void setGameFromCsv(Game voidGame, Cell[][] cellMatrix) {
        for (int x = 0; x < cellMatrix.length; x++) {
            for (int y = 0; y < cellMatrix.length; y++) {
                if (!cellMatrix[x][y].isNone()) {
                    Player playerFoundInBoard;
                    if (cellMatrix[x][y] == Stone.BLACK) {
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
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfGamePlayElements")
    void placeStone(Cell[][] matrix, Coordinates coordinates, boolean finishedGame) throws NoSuchFieldException, IllegalAccessException {
        game = new Game(matrix.length, cpuBlack, cpuWhite);
        setGameFromCsv(game, matrix);

        Field fieldBoardGame = game.getClass().getDeclaredField("board");
        fieldBoardGame.setAccessible(true);
        Board expectedBoard = TestUtility.createBoardFromCellMatrix(matrix, matrix.length);

        assertEquals(expectedBoard, fieldBoardGame.get(game));

        if (finishedGame) {
            try {
                if (expectedBoard.isThereAnyEmptyCell()) {
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
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfGamePlayElements")
    void getWinner(Stone[][] matrix, Coordinates coordinates, boolean finishedGame) {
        game = new Game(matrix.length, cpuBlack, cpuWhite);
        setGameFromCsv(game, matrix);
        try {
            Player winner = game.getWinner();
            if (game.getBoard().isThereAnyEmptyCell()) {
                assertNotNull(winner);
            } else {
                assertNull(winner);
            }
        } catch (Game.GameNotEndedException e) {
            assertFalse(finishedGame);
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfGamePlayElements")
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