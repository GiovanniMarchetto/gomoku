package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.entities.Setup;
import it.units.sdm.gomoku.model.entities.player.FakePlayer;

public class FakeMainViewmodel extends MainViewmodel {

    public static final Player player1 = new FakePlayer();
    public static final Player player2 = new FakePlayer();
    public static final PositiveInteger numberOfGames = new PositiveInteger(3);
    public static final PositiveInteger boardSize = new PositiveInteger(5);
    public static final Setup setup = new Setup(player1, player2, numberOfGames, boardSize);

    @Override
    public void startNewMatch() {
        createMatchFromSetupAndInitializeNewGame(setup);
    }

    @Override
    public void endGame() {
    }
}
