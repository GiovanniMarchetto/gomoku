package it.units.sdm.gomoku.model.entities.game;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;

import java.util.ArrayList;
import java.util.List;

public class GameTestUtility {

    public static void disputeGameWithSmartAlgorithm(Game game)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        final CPUPlayer cpuPlayer = new CPUPlayer();
        cpuPlayer.setCurrentGame(game);
        while (!game.isEnded()) {
            game.placeStoneAndChangeTurn(cpuPlayer.chooseEmptyCoordinatesSmartly());
        }
    }

    public static void disputeGameAndDraw(Game game)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        List<Coordinates> remainCoordinates = new ArrayList<>();
        int boardSize = game.getBoardSize();
        for (int x = 0; x < boardSize; x++) {
            for (int y = 0; y < boardSize; y++) {
                if (x % 3 == 0 && y == 0) {
                    game.placeStoneAndChangeTurn(new Coordinates(x, y));
                } else {
                    remainCoordinates.add(new Coordinates(x, y));
                }
            }
        }

        for (Coordinates c : remainCoordinates) {
            game.placeStoneAndChangeTurn(c);
        }
    }

    public static void disputeGameAndMakeThePlayerToWin(Game game, Player player)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        placeTwoChainOfFourIn0And1Rows(game);

        if (player == game.getCurrentPlayerProperty().getPropertyValue()) {
            game.placeStoneAndChangeTurn(new Coordinates(0, 4));
        } else {
            game.placeStoneAndChangeTurn(new Coordinates(2, 0));
            game.placeStoneAndChangeTurn(new Coordinates(1, 4));
        }
    }

    static void placeTwoChainOfFourIn0And1Rows(Game game)
            throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {

        for (int i = 0; i < 4; i++) {
            game.placeStoneAndChangeTurn(new Coordinates(0, i));
            game.placeStoneAndChangeTurn(new Coordinates(1, i));
        }
    }
}
