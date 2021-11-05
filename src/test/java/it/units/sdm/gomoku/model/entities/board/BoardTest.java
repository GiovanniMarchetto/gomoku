package it.units.sdm.gomoku.model.entities.board;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Stone;
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
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.*;
import static it.units.sdm.gomoku.utils.TestUtility.getStreamOfGamePlayElements;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

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

    @BeforeEach
    void setup() {
        board = TestUtility.createBoardWithCsvBoardStone();
    }

    @Test
    void getSize() {
        assertEquals(EnvVariables.BOARD_SIZE.intValue(), board.getSize());
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#provideCoupleOfNonNegativeIntegersTillBoardSize")
    void getStoneAtCoordinates(int x, int y) {
        assertEquals(TestUtility.boardStoneFromCsv[x][y], board.getStoneAtCoordinates(new Coordinates(x, y)));
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#provideCoupleOfNonNegativeIntegersTillBoardSize")
    void getBoardMatrixCopy(int x, int y) {
        assertEquals(TestUtility.boardStoneFromCsv[x][y], board.getBoardMatrixCopy()[x][y]);
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void isCoordinatesInsideBoard(int value) {
        Coordinates coordinates = new Coordinates(value, value);
        assertEquals(value < EnvVariables.BOARD_SIZE.intValue(), board.isCoordinatesInsideBoard(coordinates));
    }

    @Test
    void isAnyEmptyPositionOnTheBoard() {
        assertTrue(board.isAnyEmptyPositionOnTheBoard());
    }

    @Test
    void isAnyEmptyPositionOnTheBoard_TestWhenShouldBeFalse() {
        board = new Board(EnvVariables.BOARD_SIZE);
        generateCoordinates(board.getSize()).forEach(coords -> {
            try {
                board.occupyPosition(Stone.BLACK, coords);
            } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                fail(e);
            }
        });
        assertFalse(board.isAnyEmptyPositionOnTheBoard());
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#provideCoupleOfNonNegativeIntegersTillBoardSize")
    void occupyPosition(int x, int y) {
        Coordinates coordinates = new Coordinates(x, y);
        try {
            board.occupyPosition(Stone.BLACK, coordinates);
            assertTrue(TestUtility.boardStoneFromCsv[x][y].isNone());
            assertEquals(Stone.BLACK, board.getStoneAtCoordinates(coordinates));
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            Coordinates firstCoordinateAfterFillBoard = new Coordinates(18, 17);
            assertEquals(firstCoordinateAfterFillBoard, coordinates);
        } catch (Board.PositionAlreadyOccupiedException e) {
            if (TestUtility.boardStoneFromCsv[x][y].isNone()) {
                fail();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void fwdDiagonalToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "fwdDiagonalToList", this::alternativeFwdDiagonalToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void bckDiagonalToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "bckDiagonalToList", this::alternativeBckDiagonalToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void columnToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "columnToList", this::alternativeColumnToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void rowToList(Stone[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "rowToList", this::alternativeRowToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
    }

    private boolean isMatrixPartToListMethodCorrect(Stone[][] matrix,
                                                    Coordinates coords,
                                                    String methodToTestName,
                                                    BiFunction<Board, Coordinates, List<Stone>> alternativeMethod)
            throws NoSuchMethodException, Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException, IllegalAccessException, InvocationTargetException {

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
    void checkNConsecutiveStones(Stone[][] matrix, Coordinates coordinates, boolean expected) {
        try {
            Board b = createBoardFromMatrix(matrix);
            NonNegativeInteger N = new NonNegativeInteger(5);
            assertEquals(expected, b.isCoordinatesBelongingToChainOfNStones(coordinates, N));
        } catch (IllegalArgumentException e) {
            if (!matrix[coordinates.getX()][coordinates.getY()].isNone()) {
                fail(e);
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
    }

    private Board createBoardFromMatrix(Stone[][] matrix) throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        Board b = new Board(matrix.length);
        for (var coords : generateCoordinates(b.getSize()).toList()) {
            if (!matrix[coords.getX()][coords.getY()].isNone())
                b.occupyPosition(matrix[coords.getX()][coords.getY()], coords);
        }

        return b;
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
    void testEqualsBetweenTwoDifferentBoardsWithSameNumberOfOccupiedPositions() {
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
                } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
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