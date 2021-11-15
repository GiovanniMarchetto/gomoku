package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class GameTestUtility {

    static void tryToPlaceStoneAndChangeTurn(Coordinates coordinates, Game game) {
        try {
            game.placeStoneAndChangeTurn(coordinates);
        } catch (Board.BoardIsFullException | Game.GameEndedException | Board.CellAlreadyOccupiedException | Board.CellOutOfBoardException e) {
            fail(e);
        }
    }

    public static void disputeGameWithSmartAlgorithm(Game game) {
        final CPUPlayer cpuPlayer = new CPUPlayer();
        while (!game.isEnded()) {
            try {
                tryToPlaceStoneAndChangeTurn(cpuPlayer.chooseSmartEmptyCoordinates(game), game);
            } catch (Board.BoardIsFullException e) {
                fail(e);
            }
        }
    }

    public static void disputeGameAndDraw(Game game, int boardSize) {
        List<Coordinates> remainCoordinates = new ArrayList<>();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (x % 3 == 0 && y == 0) {
                    tryToPlaceStoneAndChangeTurn(new Coordinates(x, y), game);
                } else {
                    remainCoordinates.add(new Coordinates(x, y));
                }
            }
        }

        for (Coordinates c : remainCoordinates) {
            tryToPlaceStoneAndChangeTurn(c, game);
        }
    }

    public static void disputeGameAndPlayerWin(Game game, Player player) {
        placeTwoChainOfFourIn0And1Rows(game);

        if (player == game.getCurrentPlayer().getPropertyValue()) {
            tryToPlaceStoneAndChangeTurn(new Coordinates(0, 4), game);
        } else {
            tryToPlaceStoneAndChangeTurn(new Coordinates(2, 0), game);
            tryToPlaceStoneAndChangeTurn(new Coordinates(1, 4), game);
        }
    }

    static void placeTwoChainOfFourIn0And1Rows(Game game) {
        try {
            for (int i = 0; i < 4; i++) {
                game.placeStoneAndChangeTurn(new Coordinates(0, i));
                game.placeStoneAndChangeTurn(new Coordinates(1, i));
            }
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameEndedException | Board.CellOutOfBoardException e) {
            fail(e);
        }
    }
}
