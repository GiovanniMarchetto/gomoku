package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Cell;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.exceptions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class GameTestBasedOnCsv {
    private Game game;

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void disputeGameAndCalculateIfThePlayerWonWithLastMove(
            Cell[][] matrix, Coordinates coordinatesToControl, boolean expectedResultOfAlgorithm)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        game = GameTestUtility.createAndDisputeGameFromCsv(matrix, coordinatesToControl);
        Method method = Game.class.getDeclaredMethod("hasThePlayerWonWithLastMove", Coordinates.class);
        method.setAccessible(true);
        boolean result = (boolean) method.invoke(game, coordinatesToControl);
        assertEquals(expectedResultOfAlgorithm, result);
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void setEndedIfThereIsAWinnerOrBoardIsFull(Cell[][] matrix, Coordinates coordinatesToControl,
                                               @SuppressWarnings("unused") boolean ignored, boolean finishedGame)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        game = GameTestUtility.createAndDisputeGameFromCsv(matrix, coordinatesToControl);
        assertEquals(finishedGame, game.isEnded());
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void disputeGameAndThrowGameNotEndedExceptionWhenGetWinnerIfGameNotEnded(
            Cell[][] matrix, Coordinates coordinatesToControl, @SuppressWarnings("unused") boolean ignored, boolean finishedGame)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        if (!finishedGame) {
            game = GameTestUtility.createAndDisputeGameFromCsv(matrix, coordinatesToControl);
            try {
                game.getWinner();
                fail("The game is not ended!");
            } catch (GameNotEndedException ignored1) {
            }
        }
    }

    @ParameterizedTest
    @MethodSource("it.units.sdm.gomoku.utils.TestUtility#getStreamOfMoveControlRecordFields")
    void disputeCompleteGameAndSetWinnerIfThereIsAWinner(Cell[][] matrix, Coordinates coordinatesToControl,
                                                         boolean isThereAWinner, boolean finishedGame)
            throws GameNotEndedException, BoardIsFullException, GameEndedException,
            CellOutOfBoardException, CellAlreadyOccupiedException {

        if (finishedGame) {
            game = GameTestUtility.createAndDisputeGameFromCsv(matrix, coordinatesToControl);
            Player winner = game.getWinner();
            assertEquals(isThereAWinner, winner != null);
        }
    }
}
