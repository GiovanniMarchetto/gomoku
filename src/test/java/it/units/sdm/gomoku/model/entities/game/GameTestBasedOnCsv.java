package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.*;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameTestBasedOnCsv {
    private Game game;

    private static Game createAndDisputeGameFromCsv(Cell[][] cellMatrix, Coordinates coordinatesToControl)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        Game game = new Game(new PositiveInteger(cellMatrix.length), new CPUPlayer(), new CPUPlayer());
        game.start();

        Board board = TestUtility.createBoardFromCellMatrix(cellMatrix);

        List<Coordinates> blackCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Color.BLACK);
        List<Coordinates> whiteCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Color.WHITE);

        for (int i = 0; i < blackCoordinatesList.size(); i++) {
            game.placeStoneAndChangeTurn(blackCoordinatesList.get(i));
            if (whiteCoordinatesList.size() == i) {
                break;
            }
            game.placeStoneAndChangeTurn(whiteCoordinatesList.get(i));
        }
        game.placeStoneAndChangeTurn(coordinatesToControl);

        return game;
    }

    @NotNull
    private static List<Coordinates> getListOfCoordinatesOfAColor(
            Coordinates coordinatesToControl, Board board, Color color) throws CellOutOfBoardException {

        AtomicReference<CellOutOfBoardException> eventuallyThrownException = new AtomicReference<>();

        List<Coordinates> coordinatesList = IntStream.range(0, board.getSize())
                .boxed().sequential()
                .flatMap(x -> IntStream.range(0, board.getSize())
                        .mapToObj(y -> new Coordinates(x, y)))
                .filter(coords -> {
                    try {
                        //noinspection ConstantConditions //the empty cells are filtered before the color request
                        return !board.getCellAtCoordinates(coords).isEmpty() &&
                                board.getCellAtCoordinates(coords).getStone().getColor() == color;
                    } catch (CellOutOfBoardException e) {
                        eventuallyThrownException.set(e);
                        return false;
                    }
                })
                .filter(coordinates -> !coordinates.equals(coordinatesToControl))
                .toList();

        if (eventuallyThrownException.get() != null) {
            throw eventuallyThrownException.get();
        }

        return coordinatesList;
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void hasThePlayerWonWithLastMove(Cell[][] matrix, Coordinates coordinatesToControl,
                                     boolean expectedResultOfAlgorithm)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        game = createAndDisputeGameFromCsv(matrix, coordinatesToControl);
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
    void getWinnerIfGameFinished(Cell[][] matrix, Coordinates coordinatesToControl,
                                 boolean ignored, boolean finishedGame)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        game = createAndDisputeGameFromCsv(matrix, coordinatesToControl);
        try {
            if (!finishedGame) {
                game.getWinner();
                fail("The game is not ended!");
            }
        } catch (GameNotEndedException ignored1) {
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void getWinnerIfGameNotFinished(Cell[][] matrix, Coordinates coordinatesToControl,
                                    boolean isThereAWinner, boolean finishedGame)
            throws GameNotEndedException, BoardIsFullException, GameEndedException,
            CellOutOfBoardException, CellAlreadyOccupiedException {

        game = createAndDisputeGameFromCsv(matrix, coordinatesToControl);
        if (finishedGame) {
            Player winner = game.getWinner();
            assertEquals(isThereAWinner, winner != null);
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void isEnded(Cell[][] matrix, Coordinates coordinatesToControl,
                 boolean ignored, boolean finishedGame)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        game = createAndDisputeGameFromCsv(matrix, coordinatesToControl);
        assertEquals(finishedGame, game.isEnded());
    }

    @Nested
    class GameTestUtilityTest {
        @ParameterizedTest
        @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
        void dontPlaceCoordinateToControl(Cell[][] cellMatrix, Coordinates coordinatesToControl) throws CellOutOfBoardException {
            Game game = new Game(new PositiveInteger(cellMatrix.length), new CPUPlayer(), new CPUPlayer());
            game.start();

            Board board = TestUtility.createBoardFromCellMatrix(cellMatrix);

            List<Coordinates> blackCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Color.BLACK);
            List<Coordinates> whiteCoordinatesList = getListOfCoordinatesOfAColor(coordinatesToControl, board, Color.WHITE);

            if (blackCoordinatesList.size() != whiteCoordinatesList.size()) {
                Stone stone = Objects.requireNonNull(board.getCellAtCoordinates(coordinatesToControl).getStone());
                int lastStone = stone.getColor() == Color.WHITE ? 1 : 0;  // TODO: re-see this to clarify
                if (blackCoordinatesList.size() != (whiteCoordinatesList.size() + lastStone)) {
                    fail("The construction of the game in the csv is wrong");
                }
            }
        }

        @ParameterizedTest
        @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
        void testCreateAndDisputeGameFromCsv(Cell[][] matrix, Coordinates coordinatesToControl)
                throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

            Game game = createAndDisputeGameFromCsv(matrix, coordinatesToControl);
            Board expectedBoard = TestUtility.createBoardFromCellMatrix(matrix);
            assertEquals(expectedBoard.toString(), game.getBoard().toString());
        }
    }
}
