package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.game.GameTestUtility;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CPUPlayerSmartChooseEmptyCoordinatesTest { //   TODO: re-see this class

    private static final int BOARD_SIZE = 5;
    private static Game game;
    private final CPUPlayer cpuPlayer = new CPUPlayer();
    private final CPUPlayer cpuPlayerWhite = new CPUPlayer();

    @BeforeEach
    void setUp() {
        game = new Game(BOARD_SIZE, cpuPlayer, cpuPlayerWhite);
        game.start();
        cpuPlayer.setCurrentGame(game);
    }

    @Test
    void occupyTheCenterOfTheBoardIfTheBoardIsEmpty() throws BoardIsFullException {
        Coordinates expected = cpuPlayer.chooseNextEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayer.chooseSmartEmptyCoordinates());
    }

    @Test
    void occupyTheCenterOfTheBoardIfThereAreNoChainInTheBoard() throws BoardIsFullException {
        occupyNStonesInARow(BOARD_SIZE, 0);
        Coordinates expected = cpuPlayer.chooseNextEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayer.chooseSmartEmptyCoordinates());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void blockTheChainOfNStones(int N) throws BoardIsFullException {
        IntStream.range(0, N).forEach(i -> occupyNStonesInARow(2, i));
        Coordinates expected = new Coordinates(N, 0);
        assertEquals(expected, cpuPlayer.chooseSmartEmptyCoordinates());
    }

    @Test
    void throwBoardIsFullExceptionIfTheBoardIsFull() {
        try {
            GameTestUtility.disputeGameAndDraw(game);
            Coordinates findCoordinates = cpuPlayer.chooseSmartEmptyCoordinates();
            fail("The board is full! But the smart choose find: " + findCoordinates);
        } catch (BoardIsFullException ignored) {
        }
    }

    private void occupyCoordinateFromXAndY(int x, int y) {
        try {
            game.placeStoneAndChangeTurn(new Coordinates(x, y));
        } catch (BoardIsFullException | CellAlreadyOccupiedException
                | CellOutOfBoardException | GameEndedException e) {
            fail(e.getMessage());
        }
    }

    private void occupyNStonesInARow(int n, int row) {
        IntStream.range(0, n)
                .forEach(col -> occupyCoordinateFromXAndY(row, col));
    }

}
