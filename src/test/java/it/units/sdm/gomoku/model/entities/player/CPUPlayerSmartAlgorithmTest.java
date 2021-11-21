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

public class CPUPlayerSmartAlgorithmTest {

    private static final int BOARD_SIZE = 5;
    private static Game game;
    private final CPUPlayer cpuPlayer = new CPUPlayer();
    private final CPUPlayer cpuPlayerNaive = new CPUPlayer("Naive", 0.0);

    @BeforeEach
    void setUp() {
        game = new Game(BOARD_SIZE, cpuPlayer, cpuPlayerNaive);
        game.start();
        cpuPlayer.setCurrentGame(game);
        cpuPlayerNaive.setCurrentGame(game);
    }

    @Test
    void chooseTheCenterOfTheBoardIfTheBoardIsEmpty() throws BoardIsFullException {
        Coordinates expected = cpuPlayer.chooseEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayer.chooseEmptyCoordinatesSmartly());
    }

    @Test
    void chooseTheCenterOfTheBoardIfThereAreNoChainInTheBoard() throws BoardIsFullException {
        occupyNCellInARow(BOARD_SIZE, 0);
        Coordinates expected = cpuPlayer.chooseEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayer.chooseEmptyCoordinatesSmartly());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void chooseTheCoordinatesConsecutiveToAChainOfMStones(int M) throws BoardIsFullException {
        createAChainOfMStonesInAColumnStartingFromFirstRow(M);
        Coordinates expected = new Coordinates(M, 0);
        assertEquals(expected, cpuPlayer.chooseEmptyCoordinatesSmartly());
    }

    private void createAChainOfMStonesInAColumnStartingFromFirstRow(int M) {
        IntStream.range(0, M).forEach(i -> occupyNCellInARow(2, i));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, BOARD_SIZE, BOARD_SIZE * BOARD_SIZE - 1})
    void chooseStoneFromCenterIfCPUHasMinimumSkill(int numberOfMoves) throws BoardIsFullException {
        IntStream.range(0, numberOfMoves).forEach(i -> {
            try {
                game.placeStoneAndChangeTurn(cpuPlayer.chooseEmptyCoordinatesFromCenter());
            } catch (BoardIsFullException | CellAlreadyOccupiedException | GameEndedException | CellOutOfBoardException e) {
                fail(e);
            }
        });
        Coordinates expected = cpuPlayer.chooseEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayerNaive.chooseEmptyCoordinatesSmartly());
    }

    @Test
    void throwExceptionWhenChoosingSmartlyNextCoordinatesIfTheBoardIsFull() {
        try {
            GameTestUtility.disputeGameAndDraw(game);
            Coordinates findCoordinates = cpuPlayer.chooseEmptyCoordinatesSmartly();
            fail("The board is full! But the smart choose find: " + findCoordinates);
        } catch (BoardIsFullException ignored) {
        }
    }

    private void occupyNCellInARow(int n, int row) {
        IntStream.range(0, n).forEach(col -> {
            try {
                game.placeStoneAndChangeTurn(new Coordinates(row, col));
            } catch (BoardIsFullException | CellAlreadyOccupiedException
                    | CellOutOfBoardException | GameEndedException e) {
                fail(e.getMessage());
            }
        });
    }

}
