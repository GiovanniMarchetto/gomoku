package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.model.exceptions.NoGameSetException;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
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
    private void setCurrentGameAndSetNextMoveAndMakeMove()  // TODO: still needed?
            throws NoGameSetException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        humanPlayer.setCurrentGame(game);
        humanPlayer.setNextMove(firstCoordinates, game);
        humanPlayer.makeMove();
    }
    //endregion Support Methods


    @BeforeEach
    void setup() {
        humanPlayer = new HumanPlayer("human");
        game = new Game(boardSize, humanPlayer, humanWhite);
        game.start();
    }

    @Test
    void dontRequireMoveToFirstHumanPlayerAfterHasDoneTheFirstMove()
            throws NoSuchFieldException, IllegalAccessException, NoGameSetException,
            GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        setCurrentGameAndSetNextMoveAndMakeMove();

        @SuppressWarnings("unchecked")
        ObservableProperty<Boolean> coordinatesRequiredToContinueProperty =
                (ObservableProperty<Boolean>) TestUtility.getFieldValue(
                        "coordinatesRequiredToContinueProperty", humanPlayer);

        //noinspection ConstantConditions
        assertEquals(Boolean.FALSE, coordinatesRequiredToContinueProperty.getPropertyValue());
    }

    @Test
    void updateBoardAfterAValidFirstMoveIsMade()
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException, NoGameSetException {
        assert game.getBoard().isEmpty();
        humanPlayer.setNextMove(firstCoordinates);
        humanPlayer.makeMove();
        assertFalse(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
    }

    @Test
    void doNotAcceptInvalidCoordinate() throws GameEndedException, CellAlreadyOccupiedException {
        try {
            humanPlayer.setNextMove(outOfBoundCoordinates);
            fail("Coordinates out of board but accepted");
        } catch (CellOutOfBoardException ignored) {   // correct to go here to pass the test
        }
    }

}