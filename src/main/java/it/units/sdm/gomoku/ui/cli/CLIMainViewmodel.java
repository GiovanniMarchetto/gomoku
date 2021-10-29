package it.units.sdm.gomoku.ui.cli;

import java.io.IOException;

public class CLIMainViewmodel extends AbstractMainViewmodel {

    @Override
    protected void startNewMatch() {
        try {
            new CLIMainView(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
