package it.units.sdm.gomoku.model.entities.board;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;
import static it.units.sdm.gomoku.utils.TestUtility.getStreamOfMoveControlRecordFields;
import static org.junit.jupiter.api.Assertions.*;

public class BoardAlgorithmTest {

    //region Support Methods
    @NotNull
    private static Stream<Arguments> getABoardAndACoordinate() {
        return getStreamOfMoveControlRecordFields()
                .map(Arguments::get)
                .map(singleTestParams -> (Cell[][]) singleTestParams[0])
                .flatMap(boardMtx -> generateCoordinates(boardMtx.length)
                        .map(aCoord -> Arguments.of(boardMtx, aCoord)));
    }

    @NotNull
    private static Stream<Coordinates> generateCoordinates(@PositiveIntegerType int boardSize) {
        return IntStream.range(0, boardSize)
                .boxed()
                .flatMap(i -> IntStream.range(0, boardSize)
                        .mapToObj(j -> new Coordinates(i, j)));
    }

    private boolean isMatrixPartToListMethodCorrect(Cell[][] matrix, Coordinates coords, String methodToTestName,
                                                    BiFunction<Board, Coordinates, List<Cell>> alternativeMethod) {
        try {
            Method m = Board.class.getDeclaredMethod(methodToTestName, Coordinates.class);
            m.setAccessible(true);
            Board b = createBoardFromMatrix(matrix);
            @SuppressWarnings("unchecked") // invoked method returns the cast type
            List<Cell> actual = (List<Cell>) m.invoke(b, coords);
            List<Cell> expected = alternativeMethod.apply(b, coords);
            return actual.equals(expected);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            fail(e);
            return false;
        }
    }

    private List<Cell> alternativeRowToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        ArrayList<Cell> list = new ArrayList<>();
        for (int yCoord = 0; yCoord < board.getSize(); yCoord++) {
            Coordinates coord = new Coordinates(Objects.requireNonNull(coords).getX(), yCoord);
            try {
                list.add(board.getCellAtCoordinates(coord));
            } catch (CellOutOfBoardException e) {
                fail(e);
            }
        }
        return list;
    }

    private List<Cell> alternativeColumnToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        ArrayList<Cell> list = new ArrayList<>();
        for (int xCoord = 0; xCoord < board.getSize(); xCoord++) {
            Coordinates coord = new Coordinates(xCoord, Objects.requireNonNull(coords).getY());
            try {
                list.add(board.getCellAtCoordinates(coord));
            } catch (CellOutOfBoardException e) {
                fail(e);
            }
        }
        return list;
    }

    private List<Cell> alternativeFwdDiagonalToList(@NotNull final Board board, @NotNull final Coordinates coords) throws CellOutOfBoardException {
        int B = board.getSize();
        int S = Objects.requireNonNull(coords).getX() + coords.getY();
        int x = Math.min(S, B - 1);
        int y = Math.max(S - (B - 1), 0);
        ArrayList<Cell> list = new ArrayList<>();
        while (y < B && x >= 0) {
            list.add(board.getCellAtCoordinates(new Coordinates(x, y)));
            x--;
            y++;
        }
        Collections.reverse(list);
        return list;
    }

    private List<Cell> alternativeBckDiagonalToList(@NotNull final Board board, @NotNull final Coordinates coords) throws CellOutOfBoardException {
        int B = board.getSize();
        int S = Objects.requireNonNull(coords).getX() - coords.getY();
        int x = Math.max(S, 0);
        int y = -Math.min(S, 0);
        ArrayList<Cell> list = new ArrayList<>();
        while (x < B && y < B) {
            list.add(board.getCellAtCoordinates(new Coordinates(x, y)));
            x++;
            y++;
        }
        return list;
    }

    private Board createBoardFromMatrix(Cell[][] cellMatrix) {
        Board b = new Board(cellMatrix.length);
        //noinspection ConstantConditions //check in the method
        occupyAllPositionsIfValidPredicateWithGivenColor(b,
                coords -> cellMatrix[coords.getX()][coords.getY()].getStone().getColor(),   // TODO: message chain
                coords -> !cellMatrix[coords.getX()][coords.getY()].isEmpty());

        return b;
    }

    private void occupyAllPositionsIfValidPredicateWithGivenColor(
            Board b, Function<Coordinates, Color> getStoneColorFromCoords, Predicate<Coordinates> predicate) {
        generateCoordinates(b.getSize())
                .filter(predicate)
                .forEach(coords -> {
                    try {
                        b.occupyPosition(getStoneColorFromCoords.apply(coords), coords);
                    } catch (BoardIsFullException | CellAlreadyOccupiedException
                            | CellOutOfBoardException e) {
                        fail(e);
                    }
                });
    }
    //endregion

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void fwdDiagonalToList(Cell[][] matrix, Coordinates coords) {
        assertTrue(
                isMatrixPartToListMethodCorrect(matrix, coords,
                        "getFwdDiagonalContainingCoords", (board1, coords1) -> {
                            try {
                                return alternativeFwdDiagonalToList(board1, coords1);
                            } catch (CellOutOfBoardException e) {
                                fail(e);
                                return null;
                            }
                        }));
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void bckDiagonalToList(Cell[][] matrix, Coordinates coords) {
        assertTrue(
                isMatrixPartToListMethodCorrect(matrix, coords,
                        "getBckDiagonalContainingCoords", (board1, coords1) -> {
                            try {
                                return alternativeBckDiagonalToList(board1, coords1);
                            } catch (CellOutOfBoardException e) {
                                fail(e);
                                return null;
                            }
                        }));
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void columnToList(Cell[][] matrix, Coordinates coords) {
        assertTrue(
                isMatrixPartToListMethodCorrect(matrix, coords,
                        "getColumnContainingCoords", this::alternativeColumnToList));
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void rowToList(Cell[][] matrix, Coordinates coords) {
        assertTrue(
                isMatrixPartToListMethodCorrect(matrix, coords,
                        "getRowContainingCoords", this::alternativeRowToList));
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void checkNConsecutiveStones(Cell[][] matrix, Coordinates coordinates, boolean expected) {
        try {
            Board b = createBoardFromMatrix(matrix);
            NonNegativeInteger N = new NonNegativeInteger(5);
            assertEquals(expected, b.isCoordinatesBelongingToChainOfNStones(coordinates, N));
        } catch (IllegalArgumentException e) {
            if (!matrix[coordinates.getX()][coordinates.getY()].isEmpty()) {
                fail(e);
            }
        }
    }
}
