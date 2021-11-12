package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Stone;
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
    private static final PositiveInteger BOARD_SIZE_5 = new PositiveInteger(5);
    private static final PositiveInteger BOARD_SIZE_4 = new PositiveInteger(4);
    private static Board board = null;
    private static Stone.Color cpuStoneColor = Stone.Color.BLACK;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");

    private static void tryToOccupyCoordinatesChosen(int x, int y) {
        try {
            board.occupyPosition(cpuStoneColor, new Coordinates(x, y));
            cpuStoneColor = cpuStoneColor == Stone.Color.BLACK ? Stone.Color.BLACK : Stone.Color.WHITE;
        } catch (CellAlreadyOccupiedException | BoardIsFullException | Board.CellOutOfBoardException e) {
            fail(e);
        }
    }

    private void checkAndOccupyCell(int x, int y, Coordinates actual) {
        Coordinates expected = new Coordinates(x, y);
        assertEquals(expected, actual);
        tryToOccupyCoordinatesChosen(x,y);
    }

    private void checkFromCenterAndOccupyCell(int x, int y) {
        try {
            checkAndOccupyCell(x, y, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (BoardIsFullException e) {
            fail(e);
        }
    }

    @BeforeAll
    static void resetBoard() {
        board = new Board(BOARD_SIZE_5);
    }

    @RepeatedTest(NUMBER_OF_REPETITION)
    void checkRandomChosenCoordinatesReferToEmptyCell() throws Board.CellOutOfBoardException {
        try {
            Coordinates actual = cpuPlayer.chooseRandomEmptyCoordinates(board);
            assertTrue(board.getCellAtCoordinates(actual).isEmpty());
            tryToOccupyCoordinatesChosen(actual.getX(), actual.getY());
        } catch (BoardIsFullException e) {
            if (board.isThereAnyEmptyCell()) {
                fail(e);
            }
        }
    }

    @Nested
    class firstEmpty {

        @BeforeAll
        static void setup() {
            resetBoard();
        }

        @ParameterizedTest
        @CsvSource({"0,0", "0,1", "0,2", "0,3", "0,4", "1,0"})
        void chooseNextEmptyCoordinates(int x, int y) {
            try {
                checkAndOccupyCell(x, y, cpuPlayer.chooseNextEmptyCoordinates(board));
            } catch (BoardIsFullException e) {
                fail(e);
            }
        }
    }

    @Nested
    class fromCenter4x4 {
        @BeforeAll
        static void resetBoard() {
            board = new Board(BOARD_SIZE_4);
        }

        @ParameterizedTest
        @CsvSource({"1,1", "1,2", "2,1", "2,2", "0,1", "0,2", "1,0", "1,3", "2,0", "2,3", "3,1", "3,2", "0,0", "0,3", "3,0", "3,3"})
        void chooseFromCenterBoard4x4(int x, int y) {
            checkFromCenterAndOccupyCell(x, y);
        }
    }

    @Nested
    class fromCenter5x5 {
        @BeforeAll
        static void setup() {
            resetBoard();
        }

        @ParameterizedTest
        @CsvSource({"2,2", "1,2", "2,1", "2,3", "3,2", "1,1", "1,3", "3,1", "3,3", "0,2",
                "2,0", "2,4", "4,2", "0,1", "0,3", "1,0", "1,4", "3,0", "3,4", "4,1",
                "4,3", "0,0", "0,4", "4,0", "4,4"})
        void chooseFromCenterBoard5x5(int x, int y) {
            checkFromCenterAndOccupyCell(x, y);
        }
    }

    @Nested
    class fromCenter5x5WithSomeOccupy {
        @BeforeAll
        static void occupyThreeCells() {
            resetBoard();
            tryToOccupyCoordinatesChosen(0,1);
            tryToOccupyCoordinatesChosen(0,3);
            tryToOccupyCoordinatesChosen(1,2);
        }

        @ParameterizedTest
        @CsvSource({"2,2", "2,1", "2,3", "3,2", "1,1"})
        void chooseNextEmptyCoordinatesFromCenter(int x, int y) {
            checkFromCenterAndOccupyCell(x, y);
        }
    }
}