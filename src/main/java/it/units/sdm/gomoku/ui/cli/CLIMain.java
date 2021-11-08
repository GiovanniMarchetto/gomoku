package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.ui.cli.views.CLIMainView;
import it.units.sdm.gomoku.ui.cli.views.CLIStartView;
import it.units.sdm.gomoku.ui.gui.viewmodels.StartViewmodel;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMain implements Observable {
    public static final CLIMainViewmodel cliMainViewmodel = new CLIMainViewmodel();

    public static void main(String[] args) {
        launch();
    }

    public static void launch() {
        try {
            init();
        } catch (IOException e) {
            Logger.getLogger(CLIMain.class.getCanonicalName())
                    .log(Level.SEVERE, "Unable to setup CLI application", e);
        }
    }

    public static void init() throws IOException {  // TODO  : refactor/rename
        StartViewmodel startViewmodel = new StartViewmodel(cliMainViewmodel);
        new CLIMainView(startViewmodel);    // CLIMainView instance must be created before CLIStartView (otherwise it does not observe property events fired by the StartViewmodel invoked in GUIStartView)
        new CLIStartView(startViewmodel);
    }
}

