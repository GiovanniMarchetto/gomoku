package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.EnvVariables;
import it.units.sdm.gomoku.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private static final int BOARD_SIZE = 19;
    private static final Board.Stone[][] boardStone = readBoardStoneFromCSVFile(EnvVariables.BOARD_19X19_PROVIDER_RESOURCE_LOCATION);
    private static final Board board = new Board(BOARD_SIZE);

    static Board.Stone[][] readBoardStoneFromCSVFile(@NotNull String filePath) {
        final String CSV_SEPARATOR = ",";
        try {
            List<String> lines = Files.readAllLines(Paths.get(Objects.requireNonNull(BoardTest.class.getResource(filePath)).toURI()))
                    .stream().sequential()
                    .filter(aLine -> aLine.trim().charAt(0) != '#')   // avoid commented lines in CSV file
                    .collect(Collectors.toList());

            return lines.stream().sequential()
                    .map(aLine -> Arrays.stream(aLine.split(CSV_SEPARATOR))
                            .map(Board.Stone::valueOf)
                            .toArray(Board.Stone[]::new))
                    .toArray(Board.Stone[][]::new);

        } catch (IOException | URISyntaxException e) {
            fail(e);
            return new Board.Stone[0][0];
        }
    }

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

    static IntStream range() {
        return IntStream.range(0, BOARD_SIZE);
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
    void isAnyEmptyPositionOnTheBoardFalse() throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        Board board2 = new Board(BOARD_SIZE);
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                board2.occupyPosition(Board.Stone.BLACK, new Coordinates(x, y));
            }
        }
        assertFalse(board2.isAnyEmptyPositionOnTheBoard());
    }

    @ParameterizedTest
    @MethodSource("range")
    void occupyPosition(int x) {
        for (int y = 0; y < BOARD_SIZE; y++) {
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
    }

}