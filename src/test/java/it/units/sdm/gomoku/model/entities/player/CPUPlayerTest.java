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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;


public class CPUPlayerTest {

    private static final int BOARD_SIZE_5 = 5;
    private static final int BOARD_SIZE_4 = 4;
    private static final CPUPlayer cpuPlayer = new CPUPlayer();
    private static final CPUPlayer cpuPlayerNaive = new CPUPlayer("Naive", 0.0);
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

    private void setUpBoardFromSize(int boardSize) {
        game = new Game(boardSize, cpuPlayer, cpuPlayerNaive);
        game.start();
        cpuPlayer.setCurrentGame(game);
        cpuPlayerNaive.setCurrentGame(game);
    }

    @ParameterizedTest
    @CsvSource({"foo,false", "-1,false", "-0.1,false", "0,true", "0.5,true", "1,true", "1.0,true", "1.05,false", "8,false"})
    void validateStringIfIsSkillFactor(String input, boolean validSkillFactor) {
        assertEquals(validSkillFactor, CPUPlayer.isValidSkillFactorFromString(input));
    }

    @RepeatedTest(BOARD_SIZE_4 * BOARD_SIZE_4)
    void occupyNCellFromCenterInBoard4x4(RepetitionInfo repetitionInfo) throws BoardIsFullException {
        setUpBoardFromSize(BOARD_SIZE_4);

        int index = repetitionInfo.getCurrentRepetition() - 1;
        IntStream.range(0, index)
                .forEach(i -> {
                    try {
                        game.placeStoneAndChangeTurn(coordinatesInOrderFromCenterForBoard4x4[i]);
                    } catch (BoardIsFullException | CellAlreadyOccupiedException | GameEndedException | CellOutOfBoardException e) {
                        fail(e);
                        //game cannot end because the board is too small
                    }
                });
        assertEquals(coordinatesInOrderFromCenterForBoard4x4[index], cpuPlayer.chooseNextEmptyCoordinatesFromCenter());
    }

    @RepeatedTest(BOARD_SIZE_5 * BOARD_SIZE_5)
    void occupyNCellFromCenterInBoard5x5(RepetitionInfo repetitionInfo) throws BoardIsFullException {
        setUpBoardFromSize(BOARD_SIZE_5);

        int index = repetitionInfo.getCurrentRepetition() - 1;
        IntStream.range(0, index)
                .forEach(i -> {
                    try {
                        game.placeStoneAndChangeTurn(coordinatesInOrderFromCenterForBoard5x5[i]);
                    } catch (BoardIsFullException | CellAlreadyOccupiedException | GameEndedException | CellOutOfBoardException e) {
                        fail(e);
                        //game cannot end because the board is too small
                    }
                });
        assertEquals(coordinatesInOrderFromCenterForBoard5x5[index], cpuPlayer.chooseNextEmptyCoordinatesFromCenter());
    }

}