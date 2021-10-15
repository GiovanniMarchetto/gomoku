package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

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
    void chooseRandomEmptyCoordinates() {
        try {
            Coordinates coordinates = cpuPlayer.chooseRandomEmptyCoordinates(board);
            board.occupyPosition(cpuStone, coordinates);
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            if (board.isAnyEmptyPositionOnTheBoard()) {
                fail(e);
            }
        } catch (Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
    }
}