package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameTestBasedOnCsv {
    private final CPUPlayer cpuBlack = new CPUPlayer();
    private final CPUPlayer cpuWhite = new CPUPlayer();
    private Game game;

    private void setUpFromCsv(Cell[][] cellMatrix, Coordinates coordinatesToControl) {
        game = new Game(cellMatrix.length, cpuBlack, cpuWhite);
        game.start();

        Board board = TestUtility.createBoardFromCellMatrix(cellMatrix, cellMatrix.length);

        List<Coordinates> blackCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Stone.Color.BLACK);
        List<Coordinates> whiteCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Stone.Color.WHITE);

        try {
            for (int i = 0; i < blackCoordinatesList.size(); i++) {
                game.placeStoneAndChangeTurn(blackCoordinatesList.get(i));
                if (whiteCoordinatesList.size() == i) {
                    break;
                }
                game.placeStoneAndChangeTurn(whiteCoordinatesList.get(i));
            }
            game.placeStoneAndChangeTurn(coordinatesToControl);
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameEndedException e) {
            fail(e);
        }
    }

    @NotNull
    private List<Coordinates> getListOfCoordinatesOfAColor(Coordinates coordinatesToControl, Board board, Stone.Color color) {
        //noinspection ConstantConditions //the empty cells are filtered before the color request
        return IntStream.range(0, board.getSize())
                .boxed().sequential()
                .flatMap(x -> IntStream.range(0, board.getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(coords -> !board.getCellAtCoordinates(coords).isEmpty())
                .filter(coords -> board.getStoneAtCoordinates(coords).color() == color)
                .filter(coordinates -> !coordinates.equals(coordinatesToControl))
                .toList();
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void setUpFromCsvCorrectness(Cell[][] cellMatrix, Coordinates coordinatesToControl) {
        game = new Game(cellMatrix.length, cpuBlack, cpuWhite);
        game.start();

        Board board = TestUtility.createBoardFromCellMatrix(cellMatrix, cellMatrix.length);

        List<Coordinates> blackCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Stone.Color.BLACK);
        List<Coordinates> whiteCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Stone.Color.WHITE);

        if (blackCoordinatesList.size() != whiteCoordinatesList.size()) {
            Stone stone = Objects.requireNonNull(board.getStoneAtCoordinates(coordinatesToControl));
            int lastStone = stone.color() == Stone.Color.WHITE ? 1 : 0;
            if (blackCoordinatesList.size() != (whiteCoordinatesList.size() + lastStone)) {
                fail("The construction of the game in the csv is wrong");
            }
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void setUpFromCsvBoardTest(Cell[][] matrix, Coordinates coordinatesToControl) {
        setUpFromCsv(matrix, coordinatesToControl);
        Board expectedBoard = TestUtility.createBoardFromCellMatrix(matrix, matrix.length);
        assertEquals(expectedBoard.toString(), game.getBoard().toString());
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void hasThePlayerWonWithLastMove(Cell[][] matrix, Coordinates coordinatesToControl, boolean expectedResultOfAlgorithm) {
        setUpFromCsv(matrix, coordinatesToControl);
        try {
            Method method = Game.class.getDeclaredMethod("hasThePlayerWonWithLastMove", Coordinates.class);
            method.setAccessible(true);
            boolean result = (boolean) method.invoke(game, coordinatesToControl);
            assertEquals(expectedResultOfAlgorithm, result);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void getWinnerIfGameFinished(Cell[][] matrix, Coordinates coordinatesToControl, boolean ignored, boolean finishedGame) {
        setUpFromCsv(matrix, coordinatesToControl);
        try {
            if (!finishedGame) {
                game.getWinner();
                fail("The game is not ended!");
            }
        } catch (Game.GameNotEndedException ignored1) {
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void getWinnerIfGameNotFinished(Cell[][] matrix, Coordinates coordinatesToControl, boolean isThereAWinner, boolean finishedGame) {
        setUpFromCsv(matrix, coordinatesToControl);
        try {
            if (finishedGame) {
                Player winner = game.getWinner();
                assertEquals(isThereAWinner, winner != null);
            }
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void isEnded(Cell[][] matrix, Coordinates coordinatesToControl, boolean ignored, boolean finishedGame) {
        setUpFromCsv(matrix, coordinatesToControl);
        assertEquals(finishedGame, game.isEnded());
    }
}
