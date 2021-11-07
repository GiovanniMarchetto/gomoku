package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.Board.BoardIsFullException;
import static it.units.sdm.gomoku.model.entities.Board.CellAlreadyOccupiedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CPUPlayerChooseEmptyCoordinatesTest {

    private static final int BOARD_SIZE = 5;
    private static Board board = null;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");
    private final Stone.Color stoneColor = Stone.Color.BLACK;

    @BeforeEach
    void setUp() {
        board = new Board(BOARD_SIZE);
    }

    @Test
    void emptyBoard() {
        Coordinates expected = getNextEmptyCoordinates();
        assertChooseEmptyCoordinates(expected);
    }

    @Test
    void noChains() {
        occupyCoordinateFromXAndY(0, 0);

        Coordinates expected = getNextEmptyCoordinates();
        assertChooseEmptyCoordinates(expected);

    }

    @NotNull
    private Coordinates getNextEmptyCoordinates() {
        try {
            return cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board);
        } catch (BoardIsFullException e) {
            fail(e.getMessage());
            return new Coordinates(0, 0);
        }
    }

    @Test
    void chainOfTwo() {
        occupyNStonesInARow(2, 0);
        Coordinates expected = new Coordinates(0, 2);
        assertChooseEmptyCoordinates(expected);
    }

    @Test
    void chainOfThree() {
        occupyNStonesInARow(2, 0);
        occupyNStonesInARow(3, 1);
        Coordinates expected = new Coordinates(1, 3);
        assertChooseEmptyCoordinates(expected);
    }

    @Test
    void chainOfFour() {
        occupyNStonesInARow(2, 0);
        occupyNStonesInARow(3, 1);
        occupyNStonesInARow(4, 2);
        Coordinates expected = new Coordinates(2, 4);
        assertChooseEmptyCoordinates(expected);
    }   // TODO: refactor needed: parameterize "chainOfN(int N)"

    private void assertChooseEmptyCoordinates(Coordinates expected) {
        try {
            assertEquals(expected, cpuPlayer.chooseSmartEmptyCoordinates(board));
        } catch (BoardIsFullException e) {
            fail(e.getMessage());
        }
    }

    private void occupyCoordinateFromXAndY(int x, int y) {
        try {
            board.occupyPosition(stoneColor, new Coordinates(x, y));
        } catch (BoardIsFullException | CellAlreadyOccupiedException e) {
            fail(e.getMessage());
        }
    }

    private void occupyNStonesInARow(int n, int row) {
        IntStream.range(0, n)
                .forEach(col -> occupyCoordinateFromXAndY(row, col));
    }

}
