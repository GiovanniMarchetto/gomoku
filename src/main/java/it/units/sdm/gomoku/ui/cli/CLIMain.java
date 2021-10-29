package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.ui.cli.views.CLIMainView;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMain implements Observable {

    public static void main(String[] args) {
        launch();
    }

    public static void launch() {
        try {
            new CLIMainView(new CLIMainViewmodel());
        } catch (IOException e) {
            Logger.getLogger(CLIMain.class.getCanonicalName())
                    .log(Level.SEVERE, "Unable to setup CLI application", e);
        }
    }
}

