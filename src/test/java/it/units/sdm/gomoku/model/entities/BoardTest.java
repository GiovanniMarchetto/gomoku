package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
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
import static it.units.sdm.gomoku.utils.TestUtility.getStreamOfGamePlayElements;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    public static final Cell[][] boardMatrixFromCsv = TestUtility.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);

    private static Board board;

    private static Stream<Arguments> getABoardAndACoordinate() {
        return getStreamOfGamePlayElements()
                .map(Arguments::get)
                .map(singleTestParams -> (Stone[][]) singleTestParams[0])
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

    @NotNull
    public static Board createBoardWithCsvBoardStone() {
        return TestUtility.createBoardFromCellMatrix(boardMatrixFromCsv, EnvVariables.BOARD_SIZE);
    }

    @BeforeEach
    void setUp() {
        board = createBoardWithCsvBoardStone();
    }

    @Test
    void getSize() {
        assertEquals(EnvVariables.BOARD_SIZE.intValue(), board.getSize());
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#provideCoupleOfNonNegativeIntegersTillBoardSize")
    void getCellAtCoordinates(int x, int y) {
        assertEquals(boardMatrixFromCsv[x][y], board.getCellAtCoordinates(new Coordinates(x, y)));
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#provideCoupleOfNonNegativeIntegersTillBoardSize")
    void getBoardMatrixCopy(int x, int y) {
        try {
            Method getBoardMatrixCopyMethod = Board.class.getDeclaredMethod("getBoardMatrixCopy");
            getBoardMatrixCopyMethod.setAccessible(true);
            Cell[][] matrixCopy = (Cell[][]) getBoardMatrixCopyMethod.invoke(board);
            assertEquals(boardMatrixFromCsv[x][y], matrixCopy[x][y]);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void isCoordinatesInsideBoard(int value) {
        Coordinates coordinates = new Coordinates(value, value);
        assertEquals(value < EnvVariables.BOARD_SIZE.intValue(), board.isCoordinatesInsideBoard(coordinates));
    }

    @Test
    void isAnyEmptyPositionOnTheBoard() {
        assertTrue(board.isThereAnyEmptyCell());
    }

    @Test
    void isAnyEmptyPositionOnTheBoard_TestWhenShouldBeFalse() {
        board = new Board(EnvVariables.BOARD_SIZE);
        occupyAllPositionsIfValidPredicateWithGivenColor(board,
                coords-> Stone.Color.BLACK,
                coords->true);
        assertFalse(board.isThereAnyEmptyCell());
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#provideCoupleOfNonNegativeIntegersTillBoardSize")
    void occupyPosition(int x, int y) {
        Coordinates coordinates = new Coordinates(x, y);
        try {
            Stone.Color stoneColor = Stone.Color.BLACK;
            board.occupyPosition(stoneColor, coordinates);
            assertTrue(wasCellEmptyAndIsNowOccupiedWithCorrectColor(boardMatrixFromCsv[x][y], coordinates, stoneColor));
        } catch (Board.BoardIsFullException e) {
            Coordinates firstCoordinateAfterFillBoard = new Coordinates(18, 17);
            assertEquals(firstCoordinateAfterFillBoard, coordinates);
        } catch (Board.CellAlreadyOccupiedException e) {
            if (boardMatrixFromCsv[x][y].isEmpty()) {
                fail("The cell was empty");
            }
        }
    }

    private boolean wasCellEmptyAndIsNowOccupiedWithCorrectColor(Cell cell, Coordinates coordinates, Stone.Color stoneColor) {
        return cell.isEmpty() && Objects.equals(stoneColor,
                Objects.requireNonNull(board.getCellAtCoordinates(coordinates).getStone()).color());
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void fwdDiagonalToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "fwdDiagonalToList", this::alternativeFwdDiagonalToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void bckDiagonalToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "bckDiagonalToList", this::alternativeBckDiagonalToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void columnToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "columnToList", this::alternativeColumnToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void rowToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "rowToList", this::alternativeRowToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    private boolean isMatrixPartToListMethodCorrect(Cell[][] matrix,
                                                    Coordinates coords,
                                                    String methodToTestName,
                                                    BiFunction<Board, Coordinates, List<Stone>> alternativeMethod)
            throws NoSuchMethodException, Board.BoardIsFullException, Board.CellAlreadyOccupiedException, IllegalAccessException, InvocationTargetException {

        Method m = Board.class.getDeclaredMethod(methodToTestName, Coordinates.class);
        m.setAccessible(true);
        Board b = createBoardFromMatrix(matrix);
        @SuppressWarnings("unchecked") // invoked method returns the cast type
        List<Stone> actual = (List<Stone>) m.invoke(b, coords);
        List<Stone> expected = alternativeMethod.apply(b, coords);
        return actual.equals(expected);
    }

    List<Stone> alternativeRowToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        List<Stone> list = new ArrayList<>();
        for (int y = 0; y < board.getSize(); y++) {
            var c = new Coordinates(Objects.requireNonNull(coords).getX(), y);
            list.add(board.getStoneAtCoordinates(c));
        }
        return list;
    }

    List<Stone> alternativeColumnToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        List<Stone> list = new ArrayList<>();
        for (int x = 0; x < board.getSize(); x++) {
            var c = new Coordinates(x, Objects.requireNonNull(coords).getY());
            list.add(board.getStoneAtCoordinates(c));
        }
        return list;
    }

    List<Stone> alternativeFwdDiagonalToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        int B = board.getSize();
        int S = Objects.requireNonNull(coords).getX() + coords.getY();
        int x = Math.min(S, B - 1);
        int y = Math.max(S - (B - 1), 0);
        ArrayList<Stone> list = new ArrayList<>();
        while (y < B && x >= 0) {
            list.add(board.getStoneAtCoordinates(new Coordinates(x, y)));
            x--;
            y++;
        }
        Collections.reverse(list);
        return list;
    }

    List<Stone> alternativeBckDiagonalToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        int B = board.getSize();
        int S = Objects.requireNonNull(coords).getX() - coords.getY();
        int x = Math.max(S, 0);
        int y = -Math.min(S, 0);
        ArrayList<Stone> list = new ArrayList<>();
        while (x < B && y < B) {
            list.add(board.getStoneAtCoordinates(new Coordinates(x, y)));
            x++;
            y++;
        }
        return list;
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfGamePlayElements")
    void checkNConsecutiveStones(Cell[][] matrix, Coordinates coordinates, boolean expected) {
        try {
            Board b = createBoardFromMatrix(matrix);
            NonNegativeInteger N = new NonNegativeInteger(5);
            assertEquals(expected, b.isCoordinatesBelongingToChainOfNStones(coordinates, N));
        } catch (IllegalArgumentException e) {
            if (!matrix[coordinates.getX()][coordinates.getY()].isNone()) {
                fail(e);
            }
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    private Board createBoardFromMatrix(Cell[][] cellMatrix) {
        Board b = new Board(cellMatrix.length);
        occupyAllPositionsIfValidPredicateWithGivenColor(b,
                coords->cellMatrix[coords.getX()][coords.getY()].getStone().color(),
                coords -> !cellMatrix[coords.getX()][coords.getY()].isEmpty());

        return b;
    }

    private void occupyAllPositionsIfValidPredicateWithGivenColor(Board b, Function<Coordinates, Stone.Color> getStoneColorFromCoords, Predicate<Coordinates> predicate) {
        generateCoordinates(b.getSize())
                .filter(predicate)
                .forEach(coords -> {
                    try {
                        //noinspection ConstantConditions // just checked
                        b.occupyPosition(getStoneColorFromCoords.apply(coords), coords);
                    } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
                        fail(e);
                    }
                });
    }


    @Test
    void testEqualsItself() {
        assertEquals(board, board);
    }

    @Test
    void testNotEqualsNewObject() {
        assertNotEquals(board, new Object());
    }

    @Test
    void testNotEqualsDifferentSize() {
        assertNotEquals(board, new Board(board.getSize() + 1));
    }

    @Test
    void testNotEqualsEmptyBoard() {
        assertNotEquals(board, new Board(board.getSize()));
    }

    @Test
    void testEqualsNewBoard() {
        Board newBoard = board.clone();
        assertEquals(board, newBoard);
    }

    @Test
    void testEqualsBetweenTwoDifferentBoardsWithSameNumberOfOccupiedPositions() {//TODO: can be redo for simplify
        Board expectedBoard = board.clone();
        int found = 0;
        List<Coordinates> coords = generateCoordinates(board.getSize()).toList();
        for (int i = 0; i < coords.size() && found < 2; i++) {
            if (expectedBoard.getStoneAtCoordinates(coords.get(i)).isNone() || board.getStoneAtCoordinates(coords.get(i)).isNone()) {
                try {
                    switch (found) {
                        case 0 -> expectedBoard.occupyPosition(Stone.BLACK, coords.get(i));
                        case 1 -> board.occupyPosition(Stone.BLACK, coords.get(i));
                        default -> {
                        }
                    }
                } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
                    e.printStackTrace();
                }

                found++;
            }
        }
        assertNotEquals(expectedBoard, board);
    }

    @Test
    void testHashCodeItself() {
        assertEquals(board.hashCode(), board.hashCode());
    }

    @Test
    void testHashCodeInvert() {
        assertNotEquals(board.hashCode(), (new Board(board.getSize())).hashCode());
    }
}