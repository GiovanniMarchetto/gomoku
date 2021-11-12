package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

public class BoardTest {

    public static final Cell[][] boardMatrixFromCsv =
            TestUtility.readBoardOfCellsFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
    public static final PositiveInteger BOARD_SIZE = new PositiveInteger(19);
    private static Board board;

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

    @NotNull
    private Coordinates tryToOccupyNextEmptyCellAndReturnCoordinates() {
        try {
            CPUPlayer cpuPlayer = new CPUPlayer();
            Coordinates coordToOccupy = cpuPlayer.chooseNextEmptyCoordinates(board);
            board.occupyPosition(Stone.Color.BLACK, coordToOccupy);
            return coordToOccupy;
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Board.CellOutOfBoardException e) {
            fail(e);
            return null;
        }
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
    void getCoordinatesHistory() {
        try {
            @SuppressWarnings("unchecked")
            List<Coordinates> expected = (List<Coordinates>)
                    TestUtility.getFieldValue("coordinatesHistory", board);
            assertEquals(expected, board.getCoordinatesHistory());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
    }

    @Test
    void getCoordinatesHistoryWithNewStoneOnTheBoard() {
        try {
            @SuppressWarnings("unchecked")
            List<Coordinates> expected = (List<Coordinates>)
                    TestUtility.getFieldValue("coordinatesHistory", board);
            Objects.requireNonNull(expected).add(tryToOccupyNextEmptyCellAndReturnCoordinates());
            assertEquals(expected, board.getCoordinatesHistory());
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
        Coordinates expected = tryToOccupyNextEmptyCellAndReturnCoordinates();
        assertEquals(expected, board.getLastMoveCoordinatesProperty().getPropertyValue());
    }

    @Test
    void isLastMoveCoordinatesPropertyValueEqualsAtLastCoordinateInHistory() {
        List<Coordinates> coordinatesHistory = board.getCoordinatesHistory();
        assertEquals(coordinatesHistory.get(coordinatesHistory.size() - 1), board.getLastMoveCoordinatesProperty().getPropertyValue());
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
                    tryToOccupyNextEmptyCellAndReturnCoordinates();
                    assertTrue(board.isThereAnyEmptyCell());
                });
    }

    @Test
    void noMoreEmptyCell() {
        board = new Board(BOARD_SIZE);
        int totalCell = (int) Math.pow(BOARD_SIZE.intValue(), 2);
        IntStream.range(0, totalCell)
                .forEach(i -> tryToOccupyNextEmptyCellAndReturnCoordinates());
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

    private boolean wasCellEmptyAndIsNowOccupiedWithCorrectColor(Cell cell, Coordinates coordinates, Stone.Color stoneColor) throws Board.CellOutOfBoardException {
        return cell.isEmpty() && Objects.equals(stoneColor,
                Objects.requireNonNull(board.getCellAtCoordinates(coordinates).getStone()).color());
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void fwdDiagonalToList(Cell[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "getFwdDiagonalContainingCoords", (board1, coords1) -> {
                try {
                    return alternativeFwdDiagonalToList(board1, coords1);
                } catch (Board.CellOutOfBoardException e) {
                    fail(e);
                    return null;
                }
            }));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void bckDiagonalToList(Cell[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "getBckDiagonalContainingCoords", (board1, coords1) -> {
                try {
                    return alternativeBckDiagonalToList(board1, coords1);
                } catch (Board.CellOutOfBoardException e) {
                    fail(e);
                    return null;
                }
            }));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void columnToList(Cell[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "getColumnContainingCoords", this::alternativeColumnToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @MethodSource("getABoardAndACoordinate")
    void rowToList(Cell[][] matrix, Coordinates coords) {
        try {
            assertTrue(isMatrixPartToListMethodCorrect(matrix, coords, "getRowContainingCoords", this::alternativeRowToList));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    private boolean isMatrixPartToListMethodCorrect(Cell[][] matrix,
                                                    Coordinates coords,
                                                    String methodToTestName,
                                                    BiFunction<Board, Coordinates, List<Cell>> alternativeMethod)  // TODO: rename method / refactor needed
            throws NoSuchMethodException, Board.BoardIsFullException, Board.CellAlreadyOccupiedException, IllegalAccessException, InvocationTargetException {

        Method m = Board.class.getDeclaredMethod(methodToTestName, Coordinates.class);
        m.setAccessible(true);
        Board b = createBoardFromMatrix(matrix);
        @SuppressWarnings("unchecked") // invoked method returns the cast type
        List<Cell> actual = (List<Cell>) m.invoke(b, coords);
        List<Cell> expected = alternativeMethod.apply(b, coords);
        return actual.equals(expected);
    }

    List<Cell> alternativeRowToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        // TODO : refactor needed : 4 very similar methods
        return IntStream.range(0, board.getSize())
                .mapToObj(yCoord -> {
                    try {
                        return board.getCellAtCoordinates(new Coordinates(Objects.requireNonNull(coords).getX(), yCoord));
                    } catch (Board.CellOutOfBoardException e) {
                        fail(e);
                        return null;
                    }
                })
                .toList();
    }

    List<Cell> alternativeColumnToList(@NotNull final Board board, @NotNull final Coordinates coords) {
        return IntStream.range(0, board.getSize())
                .mapToObj(xCoord -> {
                    try {
                        return board.getCellAtCoordinates(new Coordinates(xCoord, Objects.requireNonNull(coords).getY()));
                    } catch (Board.CellOutOfBoardException e) {
                        fail(e);
                        return null;
                    }
                })
                .toList();
    }

    List<Cell> alternativeFwdDiagonalToList(@NotNull final Board board, @NotNull final Coordinates coords) throws Board.CellOutOfBoardException {
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

    List<Cell> alternativeBckDiagonalToList(@NotNull final Board board, @NotNull final Coordinates coords) throws Board.CellOutOfBoardException {
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

    private Board createBoardFromMatrix(Cell[][] cellMatrix) {
        Board b = new Board(cellMatrix.length);
        //noinspection ConstantConditions //check in the method
        occupyAllPositionsIfValidPredicateWithGivenColor(b,
                coords -> cellMatrix[coords.getX()][coords.getY()].getStone().color(),
                coords -> !cellMatrix[coords.getX()][coords.getY()].isEmpty());

        return b;
    }

    private void occupyAllPositionsIfValidPredicateWithGivenColor(Board b, Function<Coordinates, Stone.Color> getStoneColorFromCoords, Predicate<Coordinates> predicate) {
        generateCoordinates(b.getSize())
                .filter(predicate)
                .forEach(coords -> {
                    try {
                        b.occupyPosition(getStoneColorFromCoords.apply(coords), coords);
                    } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Board.CellOutOfBoardException e) {
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
    void testEqualsBetweenTwoDifferentBoardsWithSameNumberOfOccupiedPositions() throws Board.CellOutOfBoardException {//TODO: can be redo for simplify
        Board expectedBoard = board.clone();
        int found = 0;
        List<Coordinates> coords = generateCoordinates(board.getSize()).toList();
        for (int i = 0; i < coords.size() && found < 2; i++) {
            if (expectedBoard.getCellAtCoordinates(coords.get(i)).isEmpty()
                    || board.getCellAtCoordinates(coords.get(i)).isEmpty()) {
                try {
                    switch (found) {
                        case 0 -> expectedBoard.occupyPosition(Stone.Color.BLACK, coords.get(i));
                        case 1 -> board.occupyPosition(Stone.Color.BLACK, coords.get(i));
                        default -> {
                        }
                    }
                } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException e) {
                    fail(e);
                }

                found++;
            }
        }
        assertNotEquals(expectedBoard, board);  // TODO : is test correct?
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