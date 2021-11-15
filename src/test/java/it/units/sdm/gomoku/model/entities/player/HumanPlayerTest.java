package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HumanPlayerTest { // TODO: some of this are tests of class Player
    private final int boardSize = 5;
    private final HumanPlayer humanWhite = new HumanPlayer("white");
    private final Coordinates firstCoordinates = new Coordinates(0, 0);
    private final Coordinates outOfBoundCoordinates = new Coordinates(boardSize, boardSize);
    private HumanPlayer humanPlayer;
    private Game game;

    //region Support Methods
    @NotNull
    private Game getCurrentGameAfterMakeMove()  // TODO: still needed?
            throws NoSuchFieldException, IllegalAccessException {
        humanPlayer.makeMove(game);
//        Game currentGame;
//        do {//wait for separate thread
//            currentGame = (Game) TestUtility.getFieldValue("currentGame", humanPlayer);
//        } while (currentGame == null);
        return game;
    }
    //endregion Support Methods


    @BeforeEach
    void setup() {
        humanPlayer = new HumanPlayer("human");
        game = new Game(boardSize, humanPlayer, humanWhite);
        game.start();
    }

//    @Test // TODO: resee this test
//    void checkCurrentGameAtStart()
//            throws NoSuchFieldException, IllegalAccessException {
//        assertNull(TestUtility.getFieldValue("currentGame", humanPlayer));
//    }

//    @Test // TODO: resee this test
//    void checkCurrentGameAfterMakeMove()
//            throws NoSuchFieldException, IllegalAccessException {
//        Game currentGame = getCurrentGameAfterMakeMove();
//        assertEquals(game, currentGame);
//    }

    @Test
    void checkCoordinatesRequiredToContinuePropertyAfterMakeMove()
            throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        getCurrentGameAfterMakeMove();
        @SuppressWarnings("unchecked")
        ObservableProperty<Boolean> coordinatesRequiredToContinueProperty =
                (ObservableProperty<Boolean>) TestUtility.getFieldValue(
                        "coordinatesRequiredToContinueProperty", humanPlayer);

        Thread.sleep(100);  // TODO: rethink about the architecture_ here we have to wait for another thread (who knows which one) to update the model: is this correct?

        //noinspection ConstantConditions
        assertEquals(Boolean.TRUE, coordinatesRequiredToContinueProperty.getPropertyValue());
    }

    @Test
    void placeStoneAndCheckCoordinates()
            throws Game.GameEndedException, Board.CellOutOfBoardException,
            Board.CellAlreadyOccupiedException, InterruptedException {
        assert game.getBoard().isEmpty();
        humanPlayer.setNextMove(firstCoordinates, game);
        humanPlayer.makeMove(game);
        Thread.sleep(100);  // TODO: rethink about the architecture_ here we have to wait for another thread (who knows which one) to update the model: is this correct?
        assertFalse(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
    }

    @Test
    void placeStoneAndCheckCoordinatesRequiredToContinueProperty()
            throws Game.GameEndedException, Board.CellOutOfBoardException, Board.CellAlreadyOccupiedException,
            NoSuchFieldException, IllegalAccessException, InterruptedException {
        humanPlayer.setNextMove(firstCoordinates, game);
        Thread.sleep(100);  // TODO: rethink about the architecture_ here we have to wait for another thread (who knows which one) to update the model: is this correct?

        @SuppressWarnings("unchecked")
        ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> coordinatesRequiredToContinueProperty =
                (ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean>) TestUtility.getFieldValue(
                        "coordinatesRequiredToContinueProperty", humanPlayer);

        //noinspection ConstantConditions
        assertEquals(Boolean.FALSE, coordinatesRequiredToContinueProperty.getPropertyValue());
    }

    @Test
    void tryToInsertInvalidCoordinatesAsNextMove_shouldFail() {
        try {
            humanPlayer.setNextMove(outOfBoundCoordinates, game);
            fail("Coordinates out of board but accepted");
        } catch (Game.GameEndedException e) {
            fail(e);
        } catch (IndexOutOfBoundsException ignored) {
            // execution should arrive here to pass the test
        }
    }

}