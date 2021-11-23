package it.units.sdm.gomoku.ui.cli.viewmodels;

import it.units.sdm.gomoku.ui.MainViewmodel;
import it.units.sdm.gomoku.ui.cli.CLISceneController;

import static it.units.sdm.gomoku.ui.cli.CLISceneController.CLIViewName.*;

public class CLIMainViewmodel extends MainViewmodel {

    @Override
    public void endGame() {
        CLISceneController.passToNewView(CLI_SUMMARY_VIEW);
    }

    @Override
    public void startNewMatch() {
        CLISceneController.passToNewView(CLI_START_VIEW);
    }

    @Override
    public void initializeNewGame() {
        super.initializeNewGame();
        CLISceneController.passToNewView(CLI_MAIN_VIEW);
    }
}
