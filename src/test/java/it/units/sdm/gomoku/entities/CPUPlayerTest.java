package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CPUPlayerTest {

    private static Board board = null;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");
    private final Board.Stone cpuStone = Board.Stone.BLACK;

    @BeforeEach
    void setup() {
        board = TestUtility.createBoardWithCsvBoardStone();
    }

    @RepeatedTest(EnvVariables.INT_NUMBER_REPETITIONS_TEST)
    void chooseRandomCoordinates() {
        Coordinates coordinates = cpuPlayer.chooseRandomCoordinates(board);
        try {
            board.occupyPosition(cpuStone, coordinates);
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            assertEquals(new Coordinates(EnvVariables.BOARD_SIZE.intValue() - 1, EnvVariables.BOARD_SIZE.intValue() - 1), coordinates);
        } catch (Board.PositionAlreadyOccupiedException e) {
            fail();
        }
    }
}