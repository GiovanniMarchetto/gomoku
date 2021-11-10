package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;

import static org.junit.jupiter.api.Assertions.fail;

public class GameTestUtility {

    static void tryToPlaceStoneAndChangeTurn(Coordinates coordinates, Game game) {
        try {
            game.placeStoneAndChangeTurn(coordinates);
        } catch (Board.BoardIsFullException | Game.GameEndedException | Board.CellAlreadyOccupiedException e) {
            fail(e);
        }
    }

    public static void disputeGameWithSmartAlgorithm(Game game) {
        CPUPlayer cpuPlayer = new CPUPlayer();
        while (!game.isEnded()) {
            try {
                tryToPlaceStoneAndChangeTurn(cpuPlayer.chooseSmartEmptyCoordinates(game.getBoard()), game);
            } catch (Board.BoardIsFullException e) {
                fail(e);
            }
        }
    }

    public static void disputeGameAndDraw(Game game, int boardSize) {
        CPUPlayer cpuPlayer = new CPUPlayer();

        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (x % 3 == 0 && y == 0) {
                    tryToPlaceStoneAndChangeTurn(new Coordinates(x, y), game);
                }
            }
        }

        while (!game.isEnded()) {
            try {
                tryToPlaceStoneAndChangeTurn(cpuPlayer.chooseNextEmptyCoordinates(game.getBoard()), game);
            } catch (Board.BoardIsFullException e) {
                fail(e);
            }
        }

        try {
            if (game.getWinner() != null) {
                fail("It's not a draw");
            }
        } catch (Game.GameNotEndedException e) {
            fail(e);
        }
    }

    public static void disputeGameAndPlayerWin(Game game, Player player) {
        try {
            for (int i = 0; i < 4; i++) {
                game.placeStoneAndChangeTurn(new Coordinates(i, 0));
                game.placeStoneAndChangeTurn(new Coordinates(i, 1));
            }

            if (player == game.getCurrentPlayer().getPropertyValue()) {
                game.placeStoneAndChangeTurn(new Coordinates(4, 0));
            } else {
                game.placeStoneAndChangeTurn(new Coordinates(0, 2));
                game.placeStoneAndChangeTurn(new Coordinates(4, 1));
            }

            if (game.getWinner() != player) {
                fail("The winner is not the correct player");
            }
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameNotEndedException | Game.GameEndedException e) {
            fail(e);
        }
    }
}
