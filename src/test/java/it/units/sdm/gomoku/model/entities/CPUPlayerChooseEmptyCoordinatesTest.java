package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.Board.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CPUPlayerChooseEmptyCoordinatesTest {

    private static final int BOARD_SIZE = 5;
    private static Board board = null;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");
    private final Stone stoneColor = Stone.BLACK;

    @BeforeEach
    void setUp() {
        board = new Board(BOARD_SIZE);
    }

    @Test
    void emptyBoard() {
        try {
            Coordinates expected = cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board);
            assertEquals(expected, cpuPlayer.chooseEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void noChains() {
        try {
            occupyCoordinateFromXAndY(0, 0);

            Coordinates expected = cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board);
            assertEquals(expected, cpuPlayer.chooseEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void chainOfTwo() {
        try {
            twoStoneIntTheFirstRow();

            Coordinates expected = new Coordinates(0, 2);
            assertEquals(expected, cpuPlayer.chooseEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void chainOfThree() {
        try {
            twoStoneIntTheFirstRow();
            threeStoneIntTheSecondRow();

            Coordinates expected = new Coordinates(1, 3);
            assertEquals(expected, cpuPlayer.chooseEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void chainOfFour() {
        try {
            twoStoneIntTheFirstRow();
            threeStoneIntTheSecondRow();
            fourStoneInTheThirdRow();

            Coordinates expected = new Coordinates(2, 4);
            assertEquals(expected, cpuPlayer.chooseEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }


    private void occupyCoordinateFromXAndY(int x, int y) {
        try {
            board.occupyPosition(stoneColor, new Coordinates(x, y));
        } catch (NoMoreEmptyPositionAvailableException | PositionAlreadyOccupiedException e) {
            fail(e.getMessage());
        }
    }

    private void occupyNStonesInARow(int n, int row) {
        IntStream.range(0, n).forEach(col ->
                occupyCoordinateFromXAndY(row, col)
        );
    }

    private void twoStoneIntTheFirstRow() {
        occupyNStonesInARow(2,0);
    }

    private void threeStoneIntTheSecondRow() {
        occupyCoordinateFromXAndY(1, 0);
        occupyCoordinateFromXAndY(1, 1);
        occupyCoordinateFromXAndY(1, 2);
    }

    private void fourStoneInTheThirdRow() {
        occupyCoordinateFromXAndY(2, 0);
        occupyCoordinateFromXAndY(2, 1);
        occupyCoordinateFromXAndY(2, 2);
        occupyCoordinateFromXAndY(2, 3);
    }
}
