package it.units.sdm.gomoku.ui.cli.viewmodels;

import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.cli.CLISceneController;

import static it.units.sdm.gomoku.ui.cli.CLISceneController.CLIViewName.CLI_MAIN_VIEW;
import static it.units.sdm.gomoku.ui.cli.CLISceneController.CLIViewName.CLI_START_VIEW;

public class CLIMainViewmodel extends AbstractMainViewmodel {   // TODO : test

    @Override
    public void startNewMatch() {
        CLISceneController.passToNewView(CLI_START_VIEW);
    }

    @Override
    public void startNewGame() {
        super.startNewGame();
        CLISceneController.passToNewView(CLI_MAIN_VIEW);
    }
}
