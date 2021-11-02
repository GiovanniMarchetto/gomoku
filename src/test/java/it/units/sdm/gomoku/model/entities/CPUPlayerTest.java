package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static it.units.sdm.gomoku.model.entities.Board.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class CPUPlayerTest {

    private static Board board = null;
    private final CPUPlayer cpuPlayer = new CPUPlayer("cpuPlayer");
    private final Stone cpuStone = Stone.BLACK;

    @BeforeEach
    void setup() {
        board = TestUtility.createBoardWithCsvBoardStone();
    }


    @ParameterizedTest
    @CsvSource({"2, 0,0", "4, 1,1", "5, 2,2", "9, 4,4"})
    void chooseFromCenterFirstStone(int size, int x, int y) {
        board = new Board(size);
        Coordinates expected = new Coordinates(x, y);
        try {
            assertEquals(expected, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @CsvSource({"2, 0,1", "4, 1,2", "5, 1,1", "9, 3,3"})
    void chooseFromCenterSecondStone(int size, int x, int y) {
        board = new Board(size);
        try {
            board.occupyPosition(Stone.BLACK, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));

            Coordinates expected = new Coordinates(x, y);
            assertEquals(expected, cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (NoMoreEmptyPositionAvailableException | PositionAlreadyOccupiedException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void chooseNextEmptyCoordinates() {
        try {
            tryToOccupyCoordinatesChosen(cpuPlayer.chooseNextEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            if (board.isAnyEmptyPositionOnTheBoard()) {
                fail(e);
            }
        }
    }

    @RepeatedTest(EnvVariables.INT_NUMBER_REPETITIONS_TEST)
    void chooseNextEmptyCoordinatesFromCenter() {
        try {
            tryToOccupyCoordinatesChosen(cpuPlayer.chooseNextEmptyCoordinatesFromCenter(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            if (board.isAnyEmptyPositionOnTheBoard()) {
                fail(e);
            }
        }
    }

    @RepeatedTest(EnvVariables.INT_NUMBER_REPETITIONS_TEST)
    void chooseRandomEmptyCoordinates() {
        try {
            tryToOccupyCoordinatesChosen(cpuPlayer.chooseRandomEmptyCoordinates(board));
        } catch (NoMoreEmptyPositionAvailableException e) {
            if (board.isAnyEmptyPositionOnTheBoard()) {
                fail(e);
            }
        }
    }

    private void tryToOccupyCoordinatesChosen(Coordinates coordinates) {
        try {
            board.occupyPosition(cpuStone, coordinates);
        } catch (PositionAlreadyOccupiedException | NoMoreEmptyPositionAvailableException e) {
            fail(e);
        }
    }

}