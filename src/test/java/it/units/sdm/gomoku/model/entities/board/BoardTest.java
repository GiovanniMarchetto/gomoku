package it.units.sdm.gomoku.model.entities.board;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.utils.TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded;
import static it.units.sdm.gomoku.model.utils.TestUtility.readBoardsWithWinCoordsAndResultsFromCSV;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    private static Board board = null;

    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded() {
        return provideCoupleOfNonNegativeIntegersTillNExcluded(EnvVariables.BOARD_SIZE.intValue());
    }

    private static Stream<Arguments> readBoardsWithWinCoordsAndResultsFromSampleCSV() {
        return readBoardsWithWinCoordsAndResultsFromCSV(EnvVariables.END_GAMES);
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
    @MethodSource("provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded")
    void getStoneAtCoordinates(int x, int y) {
        assertEquals(TestUtility.boardStoneFromCsv[x][y], board.getStoneAtCoordinates(new Coordinates(x, y)));
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded")
    void getBoard(int x, int y) {
        assertEquals(TestUtility.boardStoneFromCsv[x][y], board.getBoard()[x][y]);
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
        Board board2 = new Board(EnvVariables.BOARD_SIZE);
        for (int x = 0; x < EnvVariables.BOARD_SIZE.intValue(); x++) {
            for (int y = 0; y < EnvVariables.BOARD_SIZE.intValue(); y++) {
                try {
                    board2.occupyPosition(Board.Stone.BLACK, new Coordinates(x, y));
                } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                    fail(e);
                }
            }
        }
        assertFalse(board2.isAnyEmptyPositionOnTheBoard());
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded")
    void occupyPosition(int x, int y) {
        Coordinates coordinates = new Coordinates(x, y);
        try {
            board.occupyPosition(Board.Stone.BLACK, coordinates);
            assertTrue(TestUtility.boardStoneFromCsv[x][y].isNone());
            assertEquals(Board.Stone.BLACK, board.getStoneAtCoordinates(coordinates));
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
    @MethodSource("readBoardsWithWinCoordsAndResultsFromSampleCSV")
    void checkNConsecutiveStonesNaive(Board.Stone[][] matrix, Coordinates coordinates, boolean expected) {
        Board b = new Board(matrix.length);
        try {
            for (int i = 0; i < matrix.length; i++)
                for (int j = 0; j < matrix[i].length; j++)
                    if (!matrix[i][j].isNone())
                        b.occupyPosition(matrix[i][j], new Coordinates(i, j));
        } catch (IllegalArgumentException e) {
            if (!matrix[coordinates.getX()][coordinates.getY()].isNone()) {
                fail(e);
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
        PositiveInteger N = new PositiveInteger(2);
        assertEquals(expected, Board.checkNConsecutiveStonesNaive(b, coordinates, N));
    }

    @ParameterizedTest
    @MethodSource("readBoardsWithWinCoordsAndResultsFromSampleCSV")
    void checkNConsecutiveStones(Board.Stone[][] matrix, Coordinates coordinates, boolean expected) {
        Board b = new Board(matrix.length);
        try {
            for (int i = 0; i < matrix.length; i++)
                for (int j = 0; j < matrix[i].length; j++)
                    if (!matrix[i][j].isNone())
                        b.occupyPosition(matrix[i][j], new Coordinates(i, j));
        } catch (IllegalArgumentException e) {
            if (!matrix[coordinates.getX()][coordinates.getY()].isNone()) {
                fail(e);
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
        NonNegativeInteger N = new NonNegativeInteger(2);
        assertEquals(expected, b.checkNConsecutiveStones(coordinates, N));
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
        Board newBoard = TestUtility.createBoardWithCsvBoardStone();
        assertEquals(board, newBoard);
    }

    @Test
    void testEqualsAfterSomeChanges() {
        Board expectedBoard = TestUtility.createBoardWithCsvBoardStone();
        int found = 0;
        for (int x = 0; x < BoardTest.board.getSize() && found < 2; x++) {
            for (int y = 0; y < BoardTest.board.getSize() && found < 2; y++) {
                Coordinates coordinates = new Coordinates(x, y);
                if (expectedBoard.getStoneAtCoordinates(coordinates).isNone() || board.getStoneAtCoordinates(coordinates).isNone()) {
                    try {
                        switch (found) {
                            case 0 -> expectedBoard.occupyPosition(Board.Stone.BLACK, coordinates);
                            case 1 -> board.occupyPosition(Board.Stone.BLACK, coordinates);
                            default -> {
                            }
                        }
                    } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                        e.printStackTrace();
                    }

                    found++;
                }
            }
        }
        assertNotEquals(expectedBoard, board);
    }

    @Test
    void testHashCodeItself() {
        assertEquals(board.hashCode(), board.hashCode());
    }

    @Test
    void testHashCode() {
        Board b2 = TestUtility.createBoardWithCsvBoardStone();
        assertEquals(board.hashCode(), b2.hashCode());
    }

    @Test
    void testHashCodeInvert() {
        assertNotEquals(board.hashCode(), (new Board(board.getSize())).hashCode());
    }
}