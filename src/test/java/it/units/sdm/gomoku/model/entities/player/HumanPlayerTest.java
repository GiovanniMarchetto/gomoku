package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.HumanPlayer;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class HumanPlayerTest {
    private final HumanPlayer humanWhite = new HumanPlayer("white");
    private final Coordinates SAMPLE_VALID_COORDINATES = new Coordinates(0, 0);
    private final HumanPlayer humanPlayer = new HumanPlayer("human");

    @BeforeEach
    void setup() throws GameAlreadyStartedException {
        final int BOARD_SIZE = 5;
        Game game = new Game(new PositiveInteger(BOARD_SIZE), humanPlayer, humanWhite);
        humanPlayer.setCurrentGame(game);
        game.start();
    }

    @Test
    void dontRequireCoordinatesAfterTheMoveIsMade() throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        humanPlayer.setMoveToBeMade(SAMPLE_VALID_COORDINATES);
        humanPlayer.makeMove();
        assertEquals(false, humanPlayer.getCoordinatesRequiredToContinueProperty().getPropertyValue());
    }

    @Test
    void requireCoordinatesBeforeMakingAMove() {
        Thread waitForHumanMoveAndThenMakeTheMoveThread = new Thread(() -> {
            try {
                humanPlayer.makeMove();
            } catch (BoardIsFullException | GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException e) {
                fail(e);
            }
        });
        new PropertyObserver<>(
                humanPlayer.getCoordinatesRequiredToContinueProperty(),
                evt -> {
                    assertEquals(true, evt.getNewValue());
                    waitForHumanMoveAndThenMakeTheMoveThread.interrupt();
                });
        waitForHumanMoveAndThenMakeTheMoveThread.start();
    }
}