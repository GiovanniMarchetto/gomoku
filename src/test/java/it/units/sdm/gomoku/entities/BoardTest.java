package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static Board board = null;

    static IntStream range() {
        return IntStream.range(0, EnvVariables.BOARD_SIZE.intValue());
    }

    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded() {
        return provideCoupleOfNonNegativeIntegersTillNExcluded(EnvVariables.BOARD_SIZE.intValue());
    }

    @BeforeEach
    void setup() {
        board = TestUtility.setBoardWithCsvBoardStone();
    }

    @Test
    void getSize() {
        assertEquals(EnvVariables.BOARD_SIZE, board.getSize());
    }

    @ParameterizedTest
    @MethodSource("range")
    void getStoneAtCoordinates(int x) {
        for (int y = 0; y < EnvVariables.BOARD_SIZE.intValue(); y++) {
            assertEquals(EnvVariables.boardStone[x][y], board.getStoneAtCoordinates(new Coordinates(x, y)));
        }
    }

    @Test
    void getBoard() {
        for (int x = 0; x < EnvVariables.BOARD_SIZE.intValue(); x++) {
            for (int y = 0; y < EnvVariables.BOARD_SIZE.intValue(); y++) {
                assertEquals(EnvVariables.boardStone[x][y], board.getBoard()[x][y]);
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = EnvVariables.NON_NEGATIVE_INTS_PROVIDER_RESOURCE_LOCATION)
    void isCoordinatesInsideBoard(int value) {
        Coordinates coordinates = new Coordinates(value, value);
        assertEquals(value < EnvVariables.BOARD_SIZE.intValue(), board.isCoordinatesInsideBoard(coordinates));
    }

    @Test
    void isAnyEmptyPositionOnTheBoard() {
        assertTrue(board.isAnyEmptyPositionOnTheBoard());
    }

    @Test
    void isAnyEmptyPositionOnTheBoard_TestWhenShouldBeFalse() {
        Board board2 = new Board(EnvVariables.BOARD_SIZE);
        for (int x = 0; x < EnvVariables.BOARD_SIZE.intValue(); x++) {
            for (int y = 0; y < EnvVariables.BOARD_SIZE.intValue(); y++) {
                try {
                    board2.occupyPosition(Board.Stone.BLACK, new Coordinates(x, y));
                } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
                    fail(e);
                }
            }
        }
        assertFalse(board2.isAnyEmptyPositionOnTheBoard());
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded")
    void occupyPosition(int x, int y) {
        Coordinates coordinates = new Coordinates(x, y);
        try {
            board.occupyPosition(Board.Stone.BLACK, coordinates);
            assertTrue(EnvVariables.boardStone[x][y].isNone());
            assertEquals(Board.Stone.BLACK, board.getStoneAtCoordinates(coordinates));
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            if (x != 18 && y != 17) {
                fail();
            }
        } catch (Board.PositionAlreadyOccupiedException e) {
            if (EnvVariables.boardStone[x][y].isNone()) {
                fail();
            }
        }
    }
}