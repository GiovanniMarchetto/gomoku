package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import org.junit.jupiter.api.BeforeAll;


public class CPUPlayerTest {    // TODO: resee this class

    public static final int NUMBER_OF_REPETITION = 10;
    private static final PositiveInteger BOARD_SIZE_5 = new PositiveInteger(5);
    private static final PositiveInteger BOARD_SIZE_4 = new PositiveInteger(4);
    private static Board board = null;
    private static final Color cpuStoneColor = Color.BLACK;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");

    @BeforeAll
    static void resetBoard() {
        board = new Board(BOARD_SIZE_5);
    }

//    private void checkAndOccupyCell(int x, int y, Coordinates actual) {   // TODO: re-see this tests
//        Coordinates expected = new Coordinates(x, y);
//        assertEquals(expected, actual);
//        tryToOccupyCoordinatesWithColor(board, cpuStoneColor, x, y);
//    }
//
//    private void checkFromCenterAndOccupyCell(int x, int y) {// TODO: re-see this
//        try {
//            checkAndOccupyCell(x, y, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(game));
//        } catch (BoardIsFullException e) {
//            fail(e);
//        }
//    }
//
//    @RepeatedTest(NUMBER_OF_REPETITION)
//    void checkRandomChosenCoordinatesReferToEmptyCell() throws Board.CellOutOfBoardException {
//        try {
//            Coordinates actual = cpuPlayer.chooseRandomEmptyCoordinates(board);
//            assertTrue(board.getCellAtCoordinates(actual).isEmpty());
//            tryToOccupyCoordinatesWithColor(board, cpuStoneColor, actual.getX(), actual.getY());
//        } catch (BoardIsFullException e) {
//            if (board.isThereAnyEmptyCell()) {
//                fail(e);
//            }
//        }
//    }
//
//    @Nested
//    class fromCenter4x4 {
//        @BeforeAll
//        static void resetBoard() {
//            board = new Board(BOARD_SIZE_4);
//        }
//
//        @ParameterizedTest
//        @CsvSource({"1,1", "1,2", "2,1", "2,2", "0,1", "0,2", "1,0", "1,3", "2,0", "2,3", "3,1", "3,2", "0,0", "0,3", "3,0", "3,3"})
//        void chooseFromCenterBoard4x4(int x, int y) {
//            checkFromCenterAndOccupyCell(x, y);
//        }
//    }
//
//    @Nested
//    class fromCenter5x5 {
//        @BeforeAll
//        static void setup() {
//            resetBoard();
//        }
//
//        @ParameterizedTest
//        @CsvSource({"2,2", "1,2", "2,1", "2,3", "3,2", "1,1", "1,3", "3,1", "3,3", "0,2",
//                "2,0", "2,4", "4,2", "0,1", "0,3", "1,0", "1,4", "3,0", "3,4", "4,1",
//                "4,3", "0,0", "0,4", "4,0", "4,4"})
//        void chooseFromCenterBoard5x5(int x, int y) {
//            checkFromCenterAndOccupyCell(x, y);
//        }
//    }
//
//    @Nested
//    class fromCenter5x5WithSomeOccupy {
//        @BeforeAll
//        static void occupyThreeCells() {
//            resetBoard();
//            tryToOccupyCoordinatesWithColor(board, cpuStoneColor, 0, 1);
//            tryToOccupyCoordinatesWithColor(board, cpuStoneColor, 0, 3);
//            tryToOccupyCoordinatesWithColor(board, cpuStoneColor, 1, 2);
//        }
//
//        @ParameterizedTest
//        @CsvSource({"2,2", "2,1", "2,3", "3,2", "1,1"})
//        void chooseNextEmptyCoordinatesFromCenter(int x, int y) {
//            checkFromCenterAndOccupyCell(x, y);
//        }
//    }
}