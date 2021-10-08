package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.entities.board.BoardTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CPUPlayerTest {

    Board board = new Board(EnvVariables.BOARD_SIZE);
    CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");
    Board.Stone cpuStone = Board.Stone.BLACK;

    @BeforeEach
    void setup() {
        try {
            for (int x = 0; x < EnvVariables.BOARD_SIZE.intValue(); x++) {
                for (int y = 0; y < EnvVariables.BOARD_SIZE.intValue(); y++) {
                    if (!BoardTest.boardStone[x][y].isNone())
                        board.occupyPosition(BoardTest.boardStone[x][y], new Coordinates(x, y));
                }
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            System.err.println(e.getMessage());
        }
    }

    @RepeatedTest(EnvVariables.INT_NUMBER_REPETITIONS_TEST)
    void chooseRandomCoordinates() {
        Coordinates coordinates = cpuPlayer.chooseRandomCoordinates(board, cpuStone);
        try {
            board.occupyPosition(cpuStone, coordinates);
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            assertEquals(new Coordinates(EnvVariables.BOARD_SIZE.intValue() - 1, EnvVariables.BOARD_SIZE.intValue() - 1), coordinates);
        } catch (Board.PositionAlreadyOccupiedException e) {
            fail();
        }
    }
}