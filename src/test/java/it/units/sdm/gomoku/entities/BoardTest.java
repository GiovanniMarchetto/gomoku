package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.utils.IOUtility;
import it.units.sdm.gomoku.utils.Predicates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static final int BOARD_SIZE = 19;
    private static final Board.Stone[][] boardStone = readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
    private static final Board board = new Board(BOARD_SIZE);


    @BeforeAll
    static void setup() {
        try {
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    if (!boardStone[x][y].isNone())
                        board.occupyPosition(boardStone[x][y], new Coordinates(x, y));
                }
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    void getSize() {
        assertEquals(BOARD_SIZE, board.getSize());
    }

    @ParameterizedTest
    @MethodSource("range")
    void getStoneAtCoordinates(int x) {
        for (int y = 0; y < BOARD_SIZE; y++) {
            assertEquals(boardStone[x][y], board.getStoneAtCoordinates(new Coordinates(x, y)));
        }
    }

    @Test
    void getBoard() {
        Board boardBasedOnCsv = new Board(BOARD_SIZE);
        try {
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    if (!boardStone[x][y].isNone())
                        boardBasedOnCsv.occupyPosition(boardStone[x][y], new Coordinates(x, y));
                    assertEquals(boardStone[x][y], boardBasedOnCsv.getBoard()[x][y]);
                }
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            System.err.println(e.getMessage());
        }
    }

    @ParameterizedTest
    @ValueSource(ints = {0, BOARD_SIZE / 2, BOARD_SIZE, BOARD_SIZE + 1})
    void isCoordinatesInsideBoard(int value) {
        Coordinates coordinates = new Coordinates(value, value);
        assertEquals(value < BOARD_SIZE, board.isCoordinatesInsideBoard(coordinates));
    }

    @Test
    void isAnyEmptyPositionOnTheBoard() {
        assertTrue(board.isAnyEmptyPositionOnTheBoard());
    }

    @Test
    void isAnyEmptyPositionOnTheBoard_TestWhenShouldBeFalse() {
        Board board2 = new Board(BOARD_SIZE);
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
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
            assertTrue(boardStone[x][y].isNone());
            assertEquals(Board.Stone.BLACK, board.getStoneAtCoordinates(coordinates));
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            if (x != 18 && y != 17) {
                fail();
            }
        } catch (Board.PositionAlreadyOccupiedException e) {
            if (boardStone[x][y].isNone()) {
                fail();
            }
        }
    }


    @NotNull
    private static IntStream range() {
        return IntStream.range(0, BOARD_SIZE);
    }

    @NotNull
    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded() {
        return provideCoupleOfNonNegativeIntegersTillNExcluded(BOARD_SIZE);
    }

    @NotNull
    static Board.Stone[][] readBoardStoneFromCSVFile(@NotNull String filePath) {

        Function<String[][], Board.Stone[][]> convertStringMatrixToStoneMatrix = stringMatrix ->
                Arrays.stream(stringMatrix).sequential()
                        .map(aLine -> Arrays.stream(aLine)
                                .map(Board.Stone::valueOf)
                                .toArray(Board.Stone[]::new))
                        .toArray(Board.Stone[][]::new);

        try {
            String[][] boardAsMatrixOfStrings = IOUtility.readFromCsvToStringMatrix(Objects.requireNonNull(filePath));
            return convertStringMatrixToStoneMatrix.apply(boardAsMatrixOfStrings);
        } catch (IOException | URISyntaxException e) {
            fail(e);
            return new Board.Stone[0][0];
        }
    }
}

class BoardTestTest {

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixOfCorrectSizeWasRead() {
        final int SIZE = 19;
        Board.Stone[][] board = BoardTest.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
        assertTrue(Predicates.isSquareMatrixOfGivenSize.test(board, SIZE));
    }

    @Test
    void readBoardStoneFromCSVFile_testIfMatrixContainsOnlyStones() {
        final int SIZE = 19;
        String board = Arrays.stream(BoardTest.readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION))
                .map(aRow -> Arrays.stream(aRow).map(String::valueOf).collect(Collectors.joining(IOUtility.CSV_SEPARATOR)))
                .collect(Collectors.joining(IOUtility.CSV_NEW_LINE));
        int totalNumberOfValidStonesFound = TestUtility.getTotalNumberOfValidStoneInTheGivenBoarsAsStringInCSVFormat(board);
        int expectedTotalNumberOfValidStones = SIZE*SIZE;
        assertEquals(expectedTotalNumberOfValidStones,totalNumberOfValidStonesFound);
    }

}