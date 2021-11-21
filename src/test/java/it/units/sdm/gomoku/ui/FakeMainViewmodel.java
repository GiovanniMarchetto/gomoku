package it.units.sdm.gomoku.ui;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.CPUPlayer;
import it.units.sdm.gomoku.model.entities.Setup;

public class FakeMainViewmodel extends MainViewmodel {

    public static final CPUPlayer cpuPlayer1 = new CPUPlayer();
    public static final CPUPlayer cpuPlayer2 = new CPUPlayer();
    public static final PositiveInteger numberOfGames = new PositiveInteger(3);
    public static final PositiveInteger boardSize = new PositiveInteger(5);
    public static final Setup setup = new Setup(cpuPlayer1, cpuPlayer2,
            numberOfGames, boardSize);  // TODO : tests should cover any combination of setups

    @Override
    public void startNewMatch() {
        createMatchFromSetupAndStartGame(setup);
    }

    @Override
    public void endGame() {

    }
}