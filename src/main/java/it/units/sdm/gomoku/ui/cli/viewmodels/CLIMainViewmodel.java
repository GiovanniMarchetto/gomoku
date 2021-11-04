package it.units.sdm.gomoku.ui.cli.viewmodels;

import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.cli.CLIMain;

import java.io.IOException;

public class CLIMainViewmodel extends AbstractMainViewmodel {

    @Override
    public void startNewMatch() {
        try {
            CLIMain.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startNewGame() {
        super.startNewGame();
        triggerFirstMove();
    }
}
