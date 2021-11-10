package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.mvvm_library.Observable;

import static it.units.sdm.gomoku.ui.cli.CLISceneController.CLIViewName.CLI_START_VIEW;

public class CLIMain implements Observable {

    public static void main(String[] args) {
        CLISceneController.initialize();
        CLISceneController.passToNewView(CLI_START_VIEW);
    }
}
