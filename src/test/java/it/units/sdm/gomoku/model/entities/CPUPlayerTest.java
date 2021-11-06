package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static it.units.sdm.gomoku.model.entities.Board.BoardIsFullException;
import static it.units.sdm.gomoku.model.entities.Board.CellAlreadyOccupiedException;
import static org.junit.jupiter.api.Assertions.*;

class CPUPlayerTest {

    public static final int NUMBER_OF_REPETITION = 10;
    private static final PositiveInteger BOARD_SIZE = new PositiveInteger(5);
    private static Board board = null;
    private static Stone.Color cpuStoneColor = Stone.Color.BLACK;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");

    @BeforeAll
    static void resetBoard() {
        board = new Board(BOARD_SIZE);

        tryToOccupyCoordinatesChosen(new Coordinates(0, 1));
        tryToOccupyCoordinatesChosen(new Coordinates(0, 3));
        tryToOccupyCoordinatesChosen(new Coordinates(1, 2));
    }

    private static void tryToOccupyCoordinatesChosen(Coordinates coordinates) {
        try {
            board.occupyPosition(cpuStoneColor, coordinates);
            cpuStoneColor = cpuStoneColor == Stone.Color.BLACK ? Stone.Color.BLACK : Stone.Color.WHITE;
        } catch (CellAlreadyOccupiedException | BoardIsFullException e) {
            fail(e);
        }
    }

    @RepeatedTest(NUMBER_OF_REPETITION)
    void checkRandomChosenCoordinatesReferToEmptyCell() {
        try {
            assertTrue(board.getCellAtCoordinates(cpuPlayer.chooseRandomEmptyCoordinates(board)).isEmpty());
        } catch (BoardIsFullException e) {
            if (board.isThereAnyEmptyCell()) {
                fail(e);
            }
        }
    }

    @ParameterizedTest
    @CsvSource({"2, 0,0", "4, 1,1", "5, 2,2", "9, 4,4"})
    void chooseFromCenterFirstStone(int size, int x, int y) {
        board = new Board(size);
        Coordinates expected = new Coordinates(x, y);
        try {
            assertEquals(expected, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (BoardIsFullException e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({"2, 0,1", "4, 1,2", "5, 1,1", "9, 3,3"})
    void chooseFromCenterSecondStone(int size, int x, int y) {
        board = new Board(size);
        try {
            board.occupyPosition(Stone.Color.BLACK, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));

            Coordinates expected = new Coordinates(x, y);
            assertEquals(expected, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (BoardIsFullException | CellAlreadyOccupiedException e) {
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
            } catch (BoardIsFullException e) {
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
            } catch (BoardIsFullException e) {
                fail(e);
            }
        }

    }

}