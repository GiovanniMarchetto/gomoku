package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.actors.CPUPlayer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Stone;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;

import static it.units.sdm.gomoku.model.entities.Board.BoardIsFullException;
import static it.units.sdm.gomoku.model.entities.Board.CellAlreadyOccupiedException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CPUPlayerSmartChooseEmptyCoordinatesTest {

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
        tryToAssertSmartChooseEmptyCoordinates(expected);
    }

    @Test
    void noChains() {
        occupyCoordinateFromXAndY(0, 0);

        Coordinates expected = getNextEmptyCoordinates();
        tryToAssertSmartChooseEmptyCoordinates(expected);

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

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void chainOfN(int N) {
        final int minChain = 2;
        IntStream.rangeClosed(minChain, N).forEach(i -> occupyNStonesInARow(i, i - minChain));
        Coordinates expected = new Coordinates(N - minChain, N);
        tryToAssertSmartChooseEmptyCoordinates(expected);
    }

    private void tryToAssertSmartChooseEmptyCoordinates(Coordinates expected) {
        try {
            assertEquals(expected, cpuPlayer.chooseSmartEmptyCoordinates(board));
        } catch (BoardIsFullException e) {
            fail(e.getMessage());
        }
    }

    private void occupyCoordinateFromXAndY(int x, int y) {
        try {
            board.occupyPosition(stoneColor, new Coordinates(x, y));
        } catch (BoardIsFullException | CellAlreadyOccupiedException | Board.CellOutOfBoardException e) {
            fail(e.getMessage());
        }
    }

    private void occupyNStonesInARow(int n, int row) {
        IntStream.range(0, n)
                .forEach(col -> occupyCoordinateFromXAndY(row, col));
    }

}
