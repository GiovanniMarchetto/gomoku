package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.game.GameTestUtility;
import it.units.sdm.gomoku.model.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;


public class CPUPlayerTest {

    private static final int BOARD_SIZE_5 = 5;
    private static final int BOARD_SIZE_4 = 4;
    private static final CPUPlayer cpuPlayerSmart = new CPUPlayer("Smart", CPUPlayer.MAX_SKILL_FACTOR);
    private static final CPUPlayer cpuPlayerNaive = new CPUPlayer("Naive", CPUPlayer.MIN_SKILL_FACTOR);
    private static final Coordinates[] coordinatesInOrderFromCenterForBoard5x5 = {
            new Coordinates(2, 2), new Coordinates(1, 2), new Coordinates(2, 1), new Coordinates(2, 3), new Coordinates(3, 2),
            new Coordinates(1, 1), new Coordinates(1, 3), new Coordinates(3, 1), new Coordinates(3, 3), new Coordinates(0, 2),
            new Coordinates(2, 0), new Coordinates(2, 4), new Coordinates(4, 2), new Coordinates(0, 1), new Coordinates(0, 3),
            new Coordinates(1, 0), new Coordinates(1, 4), new Coordinates(3, 0), new Coordinates(3, 4), new Coordinates(4, 1),
            new Coordinates(4, 3), new Coordinates(0, 0), new Coordinates(0, 4), new Coordinates(4, 0), new Coordinates(4, 4)};
    private static final Coordinates[] coordinatesInOrderFromCenterForBoard4x4 = {
            new Coordinates(1, 1), new Coordinates(1, 2), new Coordinates(2, 1), new Coordinates(2, 2),
            new Coordinates(0, 1), new Coordinates(0, 2), new Coordinates(1, 0), new Coordinates(1, 3),
            new Coordinates(2, 0), new Coordinates(2, 3), new Coordinates(3, 1), new Coordinates(3, 2),
            new Coordinates(0, 0), new Coordinates(0, 3), new Coordinates(3, 0), new Coordinates(3, 3)};
    private static Game game;

    @BeforeEach
    void setUpBoard() throws GameAlreadyStartedException {
        setUpBoardFromSize(BOARD_SIZE_5);
    }

    private void setUpBoardFromSize(int boardSize) throws GameAlreadyStartedException {
        game = new Game(new PositiveInteger(boardSize), cpuPlayerSmart, cpuPlayerNaive);
        game.start();
        cpuPlayerSmart.setCurrentGame(game);
        cpuPlayerNaive.setCurrentGame(game);
    }

    @ParameterizedTest
    @CsvSource({"-1,false", "-0.1,false", "0,true", "0.5,true", "1,true", "1.0,true", "1.05,false", "8,false"})
    void dontCreateIfSkillFactorNotValid(double skillFactor, boolean validSkillFactor) {
        try {
            new CPUPlayer("cpu", skillFactor);
            assertTrue(validSkillFactor);
        } catch (IllegalArgumentException ignored) {
            assertFalse(validSkillFactor);
        }
    }

    @ParameterizedTest
    @CsvSource({"-1,false", "-0.1,false", "0,true", "0.5,true", "1,true", "1.0,true", "1.05,false", "8,false"})
    void testSkillFactorValidator(double input, boolean validSkillFactor) {
        assertEquals(validSkillFactor, CPUPlayer.isValidSkillFactor(input));
    }

    @Test
    void makeFirstMoveSmartly() throws BoardIsFullException, GameEndedException, CellOutOfBoardException {
        Coordinates coordinatesSmartlyChosenFromAlgorithm = cpuPlayerSmart.chooseEmptyCoordinatesSmartly();
        assert game.isBoardEmpty();
        cpuPlayerSmart.makeMove();
        assertFalse(game.isValidMove(coordinatesSmartlyChosenFromAlgorithm));
    }

    @RepeatedTest(BOARD_SIZE_4 * BOARD_SIZE_4)
    void occupyNCellsFromCenterInBoard4x4(RepetitionInfo repetitionInfo) throws BoardIsFullException, GameAlreadyStartedException {
        setUpBoardFromSize(BOARD_SIZE_4);
        int index = repetitionInfo.getCurrentRepetition() - 1;
        occupyNCellsFromCenter(index, BOARD_SIZE_4, coordinatesInOrderFromCenterForBoard4x4);

        assertEquals(coordinatesInOrderFromCenterForBoard4x4[index],
                cpuPlayerSmart.chooseEmptyCoordinatesFromCenter());
    }

    @RepeatedTest(BOARD_SIZE_5 * BOARD_SIZE_5)
    void occupyNCellsFromCenterInBoard5x5(RepetitionInfo repetitionInfo) throws BoardIsFullException {
        int index = repetitionInfo.getCurrentRepetition() - 1;
        occupyNCellsFromCenter(index, BOARD_SIZE_5, coordinatesInOrderFromCenterForBoard5x5);
        assertEquals(coordinatesInOrderFromCenterForBoard5x5[index], cpuPlayerSmart.chooseEmptyCoordinatesFromCenter());
    }

    private void occupyNCellsFromCenter(
            int N, int boardSize, Coordinates[] coordinatesInOrderFromCenterForBoard) {

        assert N < Math.pow(boardSize, 2);
        IntStream.range(0, N)
                .forEach(i -> {
                    try {
                        game.placeStoneAndChangeTurn(coordinatesInOrderFromCenterForBoard[i]);
                    } catch (BoardIsFullException | CellAlreadyOccupiedException | GameEndedException |
                            CellOutOfBoardException | GameNotStartedException e) {
                        fail(e);
                    }
                });
    }

    @Test
    void chooseTheCenterOfTheBoardIfTheBoardIsEmpty() throws BoardIsFullException {
        Coordinates expected = cpuPlayerSmart.chooseEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayerSmart.chooseEmptyCoordinatesSmartly());
    }

    @Test
    void chooseTheCenterOfTheBoardIfThereAreNoChainsInTheBoard() throws BoardIsFullException {
        occupyNCellInARow(BOARD_SIZE_5, 0);
        Coordinates expected = cpuPlayerSmart.chooseEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayerSmart.chooseEmptyCoordinatesSmartly());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void chooseTheCoordinatesNextToAChainOfMStones(int M) throws BoardIsFullException {
        createAChainOfMStonesInAColumnStartingFromFirstRow(M);
        Coordinates expected = new Coordinates(M, 0);
        assertEquals(expected, cpuPlayerSmart.chooseEmptyCoordinatesSmartly());
    }

    private void createAChainOfMStonesInAColumnStartingFromFirstRow(int M) {
        IntStream.range(0, M).forEach(i -> occupyNCellInARow(2, i));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, BOARD_SIZE_5, BOARD_SIZE_5 * BOARD_SIZE_5 - 1})
    void chooseStoneFromCenterIfCPUHasMinimumSkill(int numberOfMoves) throws BoardIsFullException {
        IntStream.range(0, numberOfMoves).forEach(i -> {
            try {
                game.placeStoneAndChangeTurn(cpuPlayerSmart.chooseEmptyCoordinatesFromCenter());
            } catch (BoardIsFullException | CellAlreadyOccupiedException | GameEndedException |
                    CellOutOfBoardException | GameNotStartedException e) {
                fail(e);
            }
        });
        Coordinates expected = cpuPlayerSmart.chooseEmptyCoordinatesFromCenter();
        assertEquals(expected, cpuPlayerNaive.chooseEmptyCoordinatesSmartly());
    }

    @Test
    void throwExceptionWhenChoosingSmartlyNextCoordinatesIfTheBoardIsFull() {
        try {
            GameTestUtility.disputeGameAndDraw(game);
            Coordinates findCoordinates = cpuPlayerSmart.chooseEmptyCoordinatesSmartly();
            fail("The board is full! But the smart choose find: " + findCoordinates);
        } catch (BoardIsFullException ignored) {
        }
    }

    private void occupyNCellInARow(int n, int row) {
        IntStream.range(0, n).forEach(col -> {
            try {
                game.placeStoneAndChangeTurn(new Coordinates(row, col));
            } catch (BoardIsFullException | CellAlreadyOccupiedException | CellOutOfBoardException |
                    GameEndedException | GameNotStartedException e) {
                fail(e.getMessage());
            }
        });
    }
}