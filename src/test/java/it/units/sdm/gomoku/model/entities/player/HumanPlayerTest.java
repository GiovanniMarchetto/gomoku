package it.units.sdm.gomoku.model.entities.player;

import it.units.sdm.gomoku.model.actors.HumanPlayer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import it.units.sdm.gomoku.utils.TestUtility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HumanPlayerTest {
    private final int boardSize = 5;
    private final HumanPlayer humanWhite = new HumanPlayer("white");
    private final Coordinates firstCoordinates = new Coordinates(0, 0);
    private final Coordinates outOfBoundCoordinates = new Coordinates(boardSize, boardSize);
    private HumanPlayer humanPlayer;
    private Game game;

    //region Support Methods
    @NotNull
    private Game getCurrentGameAfterMakeMove()
            throws NoSuchFieldException, IllegalAccessException {
        humanPlayer.makeMove(game);
        Game currentGame;
        do {//wait for separate thread
            currentGame = (Game) TestUtility.getFieldValue("currentGame", humanPlayer);
        } while (currentGame == null);
        return currentGame;
    }
    //endregion Support Methods


    @BeforeEach
    void setup() {
        humanPlayer = new HumanPlayer("human");
        game = new Game(boardSize, humanPlayer, humanWhite);
        game.start();
    }

    @Test
    void checkCurrentGameAtStart()
            throws NoSuchFieldException, IllegalAccessException {
        assertNull(TestUtility.getFieldValue("currentGame", humanPlayer));
    }

    @Test
    void checkCurrentGameAfterMakeMove()
            throws NoSuchFieldException, IllegalAccessException {
        Game currentGame = getCurrentGameAfterMakeMove();
        assertEquals(game, currentGame);
    }

    @Test
    void checkCoordinatesRequiredToContinuePropertyAfterMakeMove()
            throws NoSuchFieldException, IllegalAccessException {
        getCurrentGameAfterMakeMove();
        @SuppressWarnings("unchecked")
        ObservableProperty<Boolean> coordinatesRequiredToContinueProperty =
                (ObservableProperty<Boolean>) TestUtility.getFieldValue(
                        "coordinatesRequiredToContinueProperty", humanPlayer);

        //noinspection ConstantConditions
        assertEquals(Boolean.TRUE, coordinatesRequiredToContinueProperty.getPropertyValue());
    }

    @Test
    void placeStoneAtStartBeforeMakeMove()
            throws Board.BoardIsFullException, Game.GameEndedException,
            Board.CellOutOfBoardException, Board.CellAlreadyOccupiedException {
        try {
            humanPlayer.placeStone(firstCoordinates);
            fail("Game not set yet");
        } catch (NullPointerException ignored) {
        }
    }

    @Test
    void placeStoneAndCheckCoordinates()
            throws Board.BoardIsFullException, Game.GameEndedException,
            Board.CellOutOfBoardException, Board.CellAlreadyOccupiedException,
            NoSuchFieldException, IllegalAccessException {
        getCurrentGameAfterMakeMove();
        humanPlayer.placeStone(firstCoordinates);
        assertFalse(game.getBoard().getCellAtCoordinates(firstCoordinates).isEmpty());
    }

    @Test
    void placeStoneAndCheckCoordinatesRequiredToContinueProperty()
            throws Board.BoardIsFullException, Game.GameEndedException,
            Board.CellOutOfBoardException, Board.CellAlreadyOccupiedException,
            NoSuchFieldException, IllegalAccessException {
        getCurrentGameAfterMakeMove();
        humanPlayer.placeStone(firstCoordinates);
        @SuppressWarnings("unchecked")
        ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> coordinatesRequiredToContinueProperty =
                (ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean>) TestUtility.getFieldValue(
                        "coordinatesRequiredToContinueProperty", humanPlayer);

        //noinspection ConstantConditions
        assertEquals(Boolean.FALSE, coordinatesRequiredToContinueProperty.getPropertyValue());
    }

    @Test
    void placeWrongStoneAndCheckCoordinatesRequiredToContinueProperty()
            throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        getCurrentGameAfterMakeMove();
        try {
            humanPlayer.placeStone(outOfBoundCoordinates);
            fail("Coordinates out of board");
        } catch (Board.CellOutOfBoardException e) {
            @SuppressWarnings("unchecked")
            ObservableProperty<Boolean> coordinatesRequiredToContinueProperty =
                    (ObservableProperty<Boolean>) TestUtility.getFieldValue(
                            "coordinatesRequiredToContinueProperty", humanPlayer);
//            while (true) {//wait for separate thread
//                //noinspection ConstantConditions
//                if (coordinatesRequiredToContinueProperty.getPropertyValue()) {
//                    break;
//                }
//            }
            //TODO: depends on thread choices
            Thread.sleep(0, 1000);
            //noinspection ConstantConditions
            assertEquals(Boolean.TRUE, coordinatesRequiredToContinueProperty.getPropertyValue());
        } catch (Board.BoardIsFullException | Game.GameEndedException | Board.CellAlreadyOccupiedException e) {
            fail(e);

        }
    }
}