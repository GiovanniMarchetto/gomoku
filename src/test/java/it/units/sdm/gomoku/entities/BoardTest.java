package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.utils.IOUtility;
import it.units.sdm.gomoku.utils.Predicates;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.utils.TestUtility.provideCoupleOfNonNegativeIntegersTillNExcluded;
import static it.units.sdm.gomoku.utils.TestUtility.readBoardsWithWinCoordsAndResultsFromCSV;
import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    public static final Board.Stone[][] boardStone = readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
    private static Board board = null;


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

    @NotNull
    public static Board setBoardWithCsvBoardStone() {
        Board board = new Board(EnvVariables.BOARD_SIZE);
        try {
            for (int x = 0; x < EnvVariables.BOARD_SIZE.intValue(); x++) {
                for (int y = 0; y < EnvVariables.BOARD_SIZE.intValue(); y++) {
                    if (!boardStone[x][y].isNone())
                        board.occupyPosition(boardStone[x][y], new Coordinates(x, y));
                }
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            System.err.println(e.getMessage());
        }
        return board;
    }

    private static Stream<Arguments> provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded() {
        return provideCoupleOfNonNegativeIntegersTillNExcluded(EnvVariables.BOARD_SIZE.intValue());
    }

    private static Stream<Arguments> readBoardsWithWinCoordsAndResultsFromSampleCSV() {
        return readBoardsWithWinCoordsAndResultsFromCSV(EnvVariables.END_GAMES);
    }

    @BeforeEach
    void setup() {
        board = setBoardWithCsvBoardStone();
    }

    @Test
    void getSize() {
        assertEquals(EnvVariables.BOARD_SIZE.intValue(), board.getSize());
    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded")
    void getStoneAtCoordinates(int x, int y) {
        assertEquals(boardStone[x][y], board.getStoneAtCoordinates(new Coordinates(x, y)));

    }

    @ParameterizedTest
    @MethodSource("provideCoupleOfNonNegativeIntegersTillBOARD_SIZEExcluded")
    void getBoard(int x, int y) {
        assertEquals(boardStone[x][y], board.getBoard()[x][y]);
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
            assertTrue(boardStone[x][y].isNone());
            assertEquals(Board.Stone.BLACK, board.getStoneAtCoordinates(coordinates));
        } catch (Board.NoMoreEmptyPositionAvailableException e) {
            Coordinates firstCoordinateAfterFillBoard = new Coordinates(18, 17);
            assertEquals(firstCoordinateAfterFillBoard, coordinates);
        } catch (Board.PositionAlreadyOccupiedException e) {
            if (boardStone[x][y].isNone()) {
                fail();
            }
        }
    }

    @ParameterizedTest
    @MethodSource("readBoardsWithWinCoordsAndResultsFromSampleCSV")
    void checkNConsecutiveStones(Board.Stone[][] matrix, Coordinates coordinates, boolean expected) {
        Board b = new Board(matrix.length);
        try {
            for (int i = 0; i < matrix.length; i++)
                for (int j = 0; j < matrix[i].length; j++)
                    if (!matrix[i][j].isNone())
                        b.occupyPosition(matrix[i][j], new Coordinates(i, j));
        } catch (IllegalArgumentException e) {
            if (!matrix[coordinates.getX()][coordinates.getY()].isNone()) {
                fail(e);
            }
        } catch (Board.NoMoreEmptyPositionAvailableException | Board.PositionAlreadyOccupiedException e) {
            fail(e);
        }
        NonNegativeInteger N = new NonNegativeInteger(2);
        assertEquals(expected, b.checkNConsecutiveStones(coordinates, N));
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
        int expectedTotalNumberOfValidStones = SIZE * SIZE;
        assertEquals(expectedTotalNumberOfValidStones, totalNumberOfValidStonesFound);
    }

}