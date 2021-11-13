package it.units.sdm.gomoku.model.entities.board;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    public static final Cell[][] boardMatrixFromCsv =
            TestUtility.readBoardOfCellsFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
    public static final PositiveInteger BOARD_SIZE = new PositiveInteger(19);
    private static Board board;

    //region Support Methods
    @NotNull
    private static Stream<Arguments> provideCoupleOfIntegersBetweenMinus10IncludedAndPlus50Excluded() {
        return TestUtility.provideCoupleOfIntegersInRange(-10, 50);
    }

    @NotNull
    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersInsideBoard() {
        return TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded(BOARD_SIZE.intValue());
    }

    @NotNull
    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersOutsideBoard() {
        return TestUtility.provideCoupleOfIntegersInRange(BOARD_SIZE.intValue(), BOARD_SIZE.intValue() + 10);
    }

    public static void tryToOccupyCoordinatesWithColor(Board board, Stone.Color color, int x, int y) {
        try {
            board.occupyPosition(color, new Coordinates(x, y));
        } catch (Board.CellAlreadyOccupiedException | Board.BoardIsFullException | Board.CellOutOfBoardException e) {
            fail(e);
        }
    }

    private Coordinates tryToOccupyNextEmptyCellAndReturnCoordinatesWithBlackStone() {
        try {
            CPUPlayer cpuPlayer = new CPUPlayer();
            Coordinates coordinatesToOccupy = cpuPlayer.chooseNextEmptyCoordinates(board);
            tryToOccupyCoordinatesWithColor(board, Stone.Color.BLACK,
                    coordinatesToOccupy.getX(), coordinatesToOccupy.getY());
            return coordinatesToOccupy;
        } catch (Board.BoardIsFullException e) {
            fail(e);
            return null;
        }
    }

    private Pair<Cell[][], Cell[][]> getInitialAndCopiedBoardMatrixWithMethodProvidedByTheClass()
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method getBoardMatrixCopyMethod = TestUtility.getMethodAlreadyMadeAccessible(board.getClass(), "getBoardMatrixCopy");
        Cell[][] toBeCopiedMatrix = (Cell[][]) TestUtility.getFieldValue("matrix", board);
        Cell[][] copiedMatrix = (Cell[][]) getBoardMatrixCopyMethod.invoke(board);
        assert toBeCopiedMatrix != null;
        return new Pair<>(toBeCopiedMatrix, copiedMatrix);
    }

    private boolean wasCellEmptyAndIsNowOccupiedWithCorrectColor(Cell cell, Coordinates coordinates, Stone.Color stoneColor) throws Board.CellOutOfBoardException {
        return cell.isEmpty() && Objects.equals(stoneColor,
                Objects.requireNonNull(board.getCellAtCoordinates(coordinates).getStone()).color());
    }

    @NotNull
    private Board setupForTestEquals() {
        board = new Board(BOARD_SIZE);
        Board board2 = new Board(board);
        tryToOccupyCoordinatesWithColor(board, Stone.Color.BLACK, 0, 0);
        tryToOccupyCoordinatesWithColor(board2, Stone.Color.WHITE, 0, 0);
        return board2;
    }
    //endregion Support Methods

    @BeforeEach
    void setUp() {
        board = TestUtility.createBoardFromCellMatrix(boardMatrixFromCsv, BOARD_SIZE);
    }

    @Test
    void getSize() {
        assertEquals(BOARD_SIZE.intValue(), board.getSize());
    }

    @Test
    void checkNumberOfFilledPositionAtCreation() {
        try {
            board = new Board(BOARD_SIZE);
            assertEquals(0, TestUtility.getFieldValue("numberOfFilledPositions", board));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @SuppressWarnings("ConstantConditions")// field declared int in the class
    @Test
    void checkNumberOfFilledPositionAfterAddAStone() {
        try {
            int expected = ((int) TestUtility.getFieldValue("numberOfFilledPositions", board)) + 1;
            tryToOccupyNextEmptyCellAndReturnCoordinatesWithBlackStone();
            int actual = ((int) TestUtility.getFieldValue("numberOfFilledPositions", board));
            assertEquals(expected, actual);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getLastMoveCoordinatesProperty() {
        try {
            @SuppressWarnings("unchecked")
            ObservableProperty<Coordinates> expected = (ObservableProperty<Coordinates>)
                    TestUtility.getFieldValue("lastMoveCoordinatesProperty", board);
            assertEquals(expected, board.getLastMoveCoordinatesProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void setLastMoveCoordinatesProperty() {
        Coordinates expected = tryToOccupyNextEmptyCellAndReturnCoordinatesWithBlackStone();
        assertEquals(expected, board.getLastMoveCoordinatesProperty().getPropertyValue());
    }

    @Test
    void isEmpty() {
        board = new Board(BOARD_SIZE);
        assertTrue(board.isEmpty());
    }

    @Test
    void checkIfIsThereAnyEmptyCellForEveryOccupyUntilBoardHasOnePositionFree() {
        board = new Board(BOARD_SIZE);
        int totalCell = (int) Math.pow(BOARD_SIZE.intValue(), 2);
        IntStream.range(0, totalCell - 1)
                .forEach(i -> {
                    tryToOccupyNextEmptyCellAndReturnCoordinatesWithBlackStone();
                    assertTrue(board.isThereAnyEmptyCell());
                });
    }

    @Test
    void noMoreEmptyCell() {
        board = new Board(BOARD_SIZE);
        int totalCell = (int) Math.pow(BOARD_SIZE.intValue(), 2);
        IntStream.range(0, totalCell)
                .forEach(i -> tryToOccupyNextEmptyCellAndReturnCoordinatesWithBlackStone());
        assertFalse(board.isThereAnyEmptyCell());
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersInsideBoard")
    void isCoordinatesInsideBoard(int value) {
        Coordinates coordinates = new Coordinates(value, value);
        assertTrue(board.isCoordinatesInsideBoard(coordinates));
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersOutsideBoard")
    void isNotCoordinatesInsideBoard(int value) {
        Coordinates coordinates = new Coordinates(value, value);
        assertFalse(board.isCoordinatesInsideBoard(coordinates));
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersInsideBoard")
    void getCellAtCoordinates(int x, int y) throws Board.CellOutOfBoardException {
        assertEquals(boardMatrixFromCsv[x][y], board.getCellAtCoordinates(new Coordinates(x, y)));
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersInsideBoard")
    void getBoardMatrixCopyTestACellToBeEqual(int x, int y) {
        try {
            Pair<Cell[][], Cell[][]> initialAndCopiedBoardMatrix =
                    getInitialAndCopiedBoardMatrixWithMethodProvidedByTheClass();
            assertEquals(initialAndCopiedBoardMatrix.getKey()[x][y], initialAndCopiedBoardMatrix.getValue()[x][y]);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersInsideBoard")
    void getBoardMatrixCopyTestToBeDeepCopy(int x, int y) {
        try {
            Pair<Cell[][], Cell[][]> initialAndCopiedBoardMatrix =
                    getInitialAndCopiedBoardMatrixWithMethodProvidedByTheClass();
            assertNotSame(initialAndCopiedBoardMatrix.getKey()[x][y], initialAndCopiedBoardMatrix.getValue()[x][y]);
        } catch (InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfIntegersBetweenMinus10IncludedAndPlus50Excluded")
    void occupyPosition(int x, int y) {
        try {
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
            } catch (Board.CellOutOfBoardException e) {
                assertFalse(board.isCoordinatesInsideBoard(coordinates));
            }
        } catch (IllegalArgumentException e) {
            assertFalse(NonNegativeInteger.isValid(x) && NonNegativeInteger.isValid(y));
        }
    }

    @Test
    void testEqualsItself() {
        assertEquals(board, board);
    }

    @Test
    void testEqualsNewObject() {
        assertNotEquals(board, new Object());
    }

    @Test
    void testEqualsEmptyBoard() {
        assertNotEquals(board, new Board(board.getSize()));
    }

    @Test
    void testEqualsNewBoard() {
        assertEquals(board, new Board(board));
    }

    @Test
    void testEqualsDifferentSize() {
        assertNotEquals(board, new Board(board.getSize() + 1));
    }

    @Test
    void testEqualsWithDifferentNumberOfFilledPosition() {
        Board board2 = setupForTestEquals();
        tryToOccupyCoordinatesWithColor(board, Stone.Color.BLACK, 0, 1);
        assertNotEquals(board2, board);
    }

    @Test
    void testEqualsWithDifferentLastMoveCoordinatesProperty() {
        Board board2 = setupForTestEquals();
        tryToOccupyCoordinatesWithColor(board, Stone.Color.BLACK, 0, 1);
        tryToOccupyCoordinatesWithColor(board2, Stone.Color.WHITE, 0, 2);
        assertNotEquals(board2, board);
    }

    @Test
    void testEqualsWithDifferentMatrix() {
        Board board2 = setupForTestEquals();
        assertNotEquals(board2, board);
    }

    @Test
    void testHashCodeItself() {
        assertEquals(board.hashCode(), board.hashCode());
    }

    @Test
    void testHashCodeNewBoard() {
        assertNotEquals(board.hashCode(), (new Board(board.getSize())).hashCode());
    }

    @Test
    void testHashCodeClone() {
        Board copy = new Board(board);
        assertNotEquals(board.hashCode(), copy.hashCode());
    }
}