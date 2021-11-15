package it.units.sdm.gomoku.model.entities.board;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Stone;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
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
    private static Stream<Arguments> provideCoupleOfIntegersBetweenMinus5IncludedAndPlus5ToBoardSizeExcluded() {
        return TestUtility.provideCoupleOfIntegersInRange(-5, BOARD_SIZE.intValue() + 5);
    }

    @NotNull
    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersInsideBoard() {
        return TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded(BOARD_SIZE.intValue());
    }

    @NotNull
    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersOutsideBoard() {
        return TestUtility.provideCoupleOfIntegersInRange(BOARD_SIZE.intValue(), BOARD_SIZE.intValue() + 10);
    }

    @NotNull
    private static Stream<Arguments> boardSupplier() {
        return Stream.of(Arguments.of(TestUtility.createBoardFromCellMatrix(boardMatrixFromCsv)));   // TODO : provide more boards
    }

    public static void tryToOccupyCoordinatesWithColor(Board board, Stone.Color color, int x, int y) {
        try {
            board.occupyPosition(color, new Coordinates(x, y));
        } catch (Board.CellAlreadyOccupiedException | Board.BoardIsFullException | Board.CellOutOfBoardException e) {
            fail(e);
        }
    }

    private static void chekFieldExistenceOrThrow(@NotNull final String fieldName,
                                                  @SuppressWarnings("SameParameterValue") @NotNull final Class<?> clazz)
            throws NoSuchFieldException {

        TestUtility.getFieldAlreadyMadeAccessible(
                Objects.requireNonNull(clazz), Objects.requireNonNull(fieldName));
    }

    private static void copyConstructorCreatesNewObjectWithEqualsButNotSameField(@NotNull final Board boardToCopy,
                                                                                 @NotNull final String fieldName,
                                                                                 boolean trueIfDifferentReferenceRequired)
            throws NoSuchFieldException, IllegalAccessException {
        Board copy = new Board(boardToCopy);
        Object fieldValueInInitial = TestUtility.getFieldValue(Objects.requireNonNull(fieldName), boardToCopy);
        Object fieldValueInCopied = TestUtility.getFieldValue(fieldName, copy);
        if (trueIfDifferentReferenceRequired) {
            Class<?> fieldType = TestUtility.getFieldAlreadyMadeAccessible(boardToCopy.getClass(), fieldName).getType();
            assertEqualityOfFieldsButDifferenceReference(fieldType, fieldValueInInitial, fieldValueInCopied);
        } else {
            assertEquals(fieldValueInInitial, fieldValueInCopied);
        }
    }

    private static void assertEqualityOfFieldsButDifferenceReference(
            @NotNull final Class<?> fieldType, final Object fieldValueInInitial, final Object fieldValueInCopied)
            throws NoSuchFieldException {
        if (fieldType.isArray()) {
            int length = Array.getLength(fieldValueInInitial);
            for (int i = 0; i < length; i++) {
                Object initialArrayElement = Array.get(fieldValueInInitial, i);
                Object copiedArrayElement = Array.get(fieldValueInCopied, i);
                for (Field f : initialArrayElement.getClass().getDeclaredFields()) {
                    Class<?> innerFieldType = TestUtility.getFieldAlreadyMadeAccessible(f.getClass(), f.getName()).getType();
                    assertEqualityOfFieldsButDifferenceReference(innerFieldType, initialArrayElement, copiedArrayElement);
                }
            }
        } else {
            assert Objects.requireNonNull(fieldValueInInitial).equals(fieldValueInCopied);
            if (!Objects.requireNonNull(fieldType).isPrimitive()) {
                assertNotSame(fieldValueInInitial, fieldValueInCopied);
            }
        }
    }

    @NotNull
    private Coordinates tryToOccupyEmptyCell() {
        try {
            CPUPlayer cpuPlayer = new CPUPlayer();
            Coordinates coordinatesToOccupy = cpuPlayer.chooseRandomEmptyCoordinates(board);
            tryToOccupyCoordinatesWithColor(board, Stone.Color.BLACK,
                    coordinatesToOccupy.getX(), coordinatesToOccupy.getY());
            return coordinatesToOccupy;
        } catch (Board.BoardIsFullException e) {
            fail(e);
            return null;
        }
    }

    @NotNull
    private Pair<Cell[][], Cell[][]> getInitialAndCopiedBoardMatrixWithMethodProvidedByTheClass()
            throws NoSuchFieldException, IllegalAccessException, InvocationTargetException {
        Method getBoardMatrixCopyMethod = TestUtility.getMethodAlreadyMadeAccessible(board.getClass(), "getBoardMatrixCopy");
        Cell[][] toBeCopiedMatrix = (Cell[][]) TestUtility.getFieldValue("matrix", board);
        Cell[][] copiedMatrix = (Cell[][]) getBoardMatrixCopyMethod.invoke(board);
        assert toBeCopiedMatrix != null;
        return new Pair<>(toBeCopiedMatrix, copiedMatrix);
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

    @ParameterizedTest
    @MethodSource("boardSupplier")
    void copyConstructorCreatesNewObject(Board boardToCopy) {
        assertNotSame(boardToCopy, new Board((boardToCopy)));
    }

    @ParameterizedTest
    @MethodSource("boardSupplier")
    void copyConstructorCreatesObjectOfSameClass(Board boardToCopy) {
        //noinspection InstantiatingObjectToGetClassObject
        assertSame(boardToCopy.getClass(), new Board((boardToCopy)).getClass());
    }

    @ParameterizedTest
    @MethodSource("boardSupplier")
    void copyConstructorCreatesObjectEqualsToTheGivenOne(Board boardToCopy) {
        assertEquals(boardToCopy, new Board(boardToCopy));
    }

    @ParameterizedTest
    @MethodSource("boardSupplier")
    void copyConstructorCreatesDeepCopyOfFields(Board boardToCopy) throws NoSuchFieldException, IllegalAccessException {
        String[] nameOfFieldsToExcludeFromDeepEquality = new String[]{"lastMoveCoordinatesProperty"};
        for (String fieldName : nameOfFieldsToExcludeFromDeepEquality) {
            chekFieldExistenceOrThrow(fieldName, Board.class);
        }
        for (Field f : boardToCopy.getClass().getDeclaredFields()) {
            copyConstructorCreatesNewObjectWithEqualsButNotSameField(
                    boardToCopy, f.getName(), !Arrays.asList(nameOfFieldsToExcludeFromDeepEquality).contains(f.getName()));
        }
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
    void checkNumberOfFilledPositionsAfterAddAStone() {
        try {
            int expected = ((int) TestUtility.getFieldValue("numberOfFilledPositions", board)) + 1;
            tryToOccupyEmptyCell();
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
            ObservablePropertyThatCanSetPropertyValueAndFireEvents<Coordinates> expected = (ObservablePropertyThatCanSetPropertyValueAndFireEvents<Coordinates>)
                    TestUtility.getFieldValue("lastMoveCoordinatesProperty", board);
            assertEquals(expected, board.getLastMoveCoordinatesProperty());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void setLastMoveCoordinatesProperty() {
        Coordinates expected = tryToOccupyEmptyCell();
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
                    tryToOccupyEmptyCell();
                    assertTrue(board.isThereAnyEmptyCell());
                });
    }

    @Test
    void noMoreEmptyCell() {
        board = new Board(BOARD_SIZE);
        int totalCell = (int) Math.pow(BOARD_SIZE.intValue(), 2);
        IntStream.range(0, totalCell)
                .forEach(i -> tryToOccupyEmptyCell());
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
    @MethodSource("provideCoupleOfIntegersBetweenMinus5IncludedAndPlus5ToBoardSizeExcluded")
    void occupyPosition(int x, int y) {
        try {
            Coordinates coordinates = new Coordinates(x, y);
            try {
                Stone.Color stoneColor = Stone.Color.BLACK;
                board.occupyPosition(stoneColor, coordinates);
                Cell cell = board.getCellAtCoordinates(coordinates);
                //noinspection ConstantConditions //check before
                assertTrue(boardMatrixFromCsv[x][y].isEmpty()
                        && !cell.isEmpty()
                        && stoneColor == cell.getStone().getColor());   // TODO: message chain
            } catch (Board.BoardIsFullException e) {
                assertFalse(board.isThereAnyEmptyCell());
            } catch (Board.CellAlreadyOccupiedException e) {
                assertFalse(boardMatrixFromCsv[x][y].isEmpty());
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
        assertEquals(board.hashCode(), copy.hashCode());
    }   // TODO : add test for hashCode - equals contract in other classes which use hashcode
}