package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @ParameterizedTest
    @CsvSource({"foo,false", "-1,false", "-0.1,false", "0,true", "0.5,true", "1,true", "1.0,true", "1.05,false", "8,false"})
    void validateStringIfIsSkillFactor(String input, boolean validSkillFactor) {
        assertEquals(validSkillFactor, CPUPlayer.isValidSkillFactorFromString(input));
    }

    @Test
    void makeASmartMove() throws BoardIsFullException, GameEndedException, CellOutOfBoardException {
        setUpBoardFromSize(BOARD_SIZE_5);
        Coordinates coordinatesSmartlyChosenFromAlgorithm = cpuPlayerSmart.chooseEmptyCoordinatesSmartly();
        assert game.isBoardEmpty();
        cpuPlayerSmart.makeMove();
        assertFalse(game.isCellAtCoordinatesEmpty(coordinatesSmartlyChosenFromAlgorithm));
    }

    @RepeatedTest(BOARD_SIZE_4 * BOARD_SIZE_4)
    void occupyNCellFromCenterInBoard4x4(RepetitionInfo repetitionInfo) throws BoardIsFullException {
        int index = repetitionInfo.getCurrentRepetition() - 1;
        occupyNCellsFromCenter(index, BOARD_SIZE_4, coordinatesInOrderFromCenterForBoard4x4);

        assertEquals(coordinatesInOrderFromCenterForBoard4x4[index],
                cpuPlayerSmart.chooseEmptyCoordinatesFromCenter());
    }

    @RepeatedTest(BOARD_SIZE_5 * BOARD_SIZE_5)
    void occupyNCellFromCenterInBoard5x5(RepetitionInfo repetitionInfo) throws BoardIsFullException {
        int index = repetitionInfo.getCurrentRepetition() - 1;
        occupyNCellsFromCenter(index, BOARD_SIZE_5, coordinatesInOrderFromCenterForBoard5x5);
        assertEquals(coordinatesInOrderFromCenterForBoard5x5[index], cpuPlayerSmart.chooseEmptyCoordinatesFromCenter());
    }

    private void occupyNCellsFromCenter(int N, int boardSize,
                                        Coordinates[] coordinatesInOrderFromCenterForBoard) {
        assert N < Math.pow(boardSize, 2);
        setUpBoardFromSize(boardSize);

        IntStream.range(0, N)
                .forEach(i -> {
                    try {
                        game.placeStoneAndChangeTurn(coordinatesInOrderFromCenterForBoard[i]);
                    } catch (BoardIsFullException | CellAlreadyOccupiedException
                            | GameEndedException | CellOutOfBoardException e) {
                        fail(e);
                    }
                });
    }

    private void setUpBoardFromSize(int boardSize) {
        game = new Game(boardSize, cpuPlayerSmart, cpuPlayerNaive);
        game.start();
        cpuPlayerSmart.setCurrentGame(game);
        cpuPlayerNaive.setCurrentGame(game);
    }

}