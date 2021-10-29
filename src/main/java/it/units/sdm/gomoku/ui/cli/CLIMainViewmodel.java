package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.ui.AbstractMainViewmodel;

import java.io.IOException;

public class CLIMainViewmodel extends AbstractMainViewmodel {

    @Override
    public void startNewMatch() {
        try {
            new CLIMainView(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
