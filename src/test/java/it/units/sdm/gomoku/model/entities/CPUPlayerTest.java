package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static it.units.sdm.gomoku.model.entities.Board.NoMoreEmptyPositionAvailableException;
import static it.units.sdm.gomoku.model.entities.Board.PositionAlreadyOccupiedException;
import static org.junit.jupiter.api.Assertions.*;

class CPUPlayerTest {

    public static final int NUMBER_OF_REPETITION = 10;
    private static final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    private static Board board = null;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");
    private Stone cpuStone = Stone.BLACK;

    @BeforeAll
    static void resetBoard() {
        board = new Board(BOARD_SIZE);
        try {
            board.occupyPosition(Stone.BLACK, new Coordinates(0, 1));
            board.occupyPosition(Stone.BLACK, new Coordinates(0, 3));
            board.occupyPosition(Stone.WHITE, new Coordinates(1, 2));
        } catch (NoMoreEmptyPositionAvailableException | PositionAlreadyOccupiedException e) {
            e.printStackTrace();
        }
    }

    @RepeatedTest(NUMBER_OF_REPETITION)
    void chooseRandomEmptyCoordinatesRepeatedTest() {
        try {
            assertTrue(board.getStoneAtCoordinates(cpuPlayer.chooseRandomEmptyCoordinates(board)).isNone());
        } catch (NoMoreEmptyPositionAvailableException e) {
            if (board.isAnyEmptyPositionOnTheBoard()) {
                fail(e);
            }
        }
    }

    private void tryToOccupyCoordinatesChosen(Coordinates coordinates) {
        try {
            board.occupyPosition(cpuStone, coordinates);
            cpuStone = cpuStone == Stone.BLACK ? Stone.BLACK : Stone.WHITE;
        } catch (PositionAlreadyOccupiedException | NoMoreEmptyPositionAvailableException e) {
            fail(e);
        }
    }

    @ParameterizedTest
    @CsvSource({"2, 0,0", "4, 1,1", "5, 2,2", "9, 4,4"})
    void chooseFromCenterFirstStone(int size, int x, int y) {
        board = new Board(size);
        Coordinates expected = new Coordinates(x, y);
        try {
            assertEquals(expected, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({"2, 0,1", "4, 1,2", "5, 1,1", "9, 3,3"})
    void chooseFromCenterSecondStone(int size, int x, int y) {
        board = new Board(size);
        try {
            board.occupyPosition(Stone.BLACK, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));

            Coordinates expected = new Coordinates(x, y);
            assertEquals(expected, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (NoMoreEmptyPositionAvailableException | PositionAlreadyOccupiedException e) {
            fail(e.getMessage());
        }
    }

    @Nested
    class NextEmptyCoordinatesTest {

        @BeforeAll
        static void tearDown() {
            resetBoard();
        }

        @ParameterizedTest
        @CsvSource({"0,0", "0,2", "0,4", "1,0"})
        void chooseNextEmptyCoordinates(int x, int y) {
            Coordinates expected = new Coordinates(x, y);
            try {
                Coordinates actual = cpuPlayer.chooseNextEmptyCoordinates(board);
                assertEquals(expected, actual);
                tryToOccupyCoordinatesChosen(actual);
            } catch (NoMoreEmptyPositionAvailableException e) {
                fail(e);
            }
        }

    }

    @Nested
    class NextEmptyCoordinatesFromCenter {

        @BeforeAll
        static void tearDown() {
            resetBoard();
        }

        @ParameterizedTest
        @CsvSource({"2,2", "1,1", "1,3", "2,1", "2,3"})
        void chooseNextEmptyCoordinatesFromCenter(int x, int y) {
            Coordinates expected = new Coordinates(x, y);
            try {
                Coordinates actual = cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board);
                assertEquals(expected, actual);
                tryToOccupyCoordinatesChosen(actual);
            } catch (NoMoreEmptyPositionAvailableException e) {
                fail(e);
            }
        }

    }

}