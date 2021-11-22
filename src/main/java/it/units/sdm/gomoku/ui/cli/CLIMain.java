package it.units.sdm.gomoku.ui.cli;

import static it.units.sdm.gomoku.ui.cli.CLISceneController.CLIViewName.CLI_START_VIEW;

public class CLIMain {

    public static void main(String[] args) {
        launch();
    }

    private static void launch() {
        CLISceneController.initialize();
        CLISceneController.passToNewView(CLI_START_VIEW);
    }
}
