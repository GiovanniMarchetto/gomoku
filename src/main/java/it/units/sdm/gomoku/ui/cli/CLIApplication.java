package it.units.sdm.gomoku.ui.cli;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class CLIApplication {

    public static void launch() {
        try {
            new CLIMainView(new CLIMainViewmodel());
        } catch (IOException e) {
            Logger.getLogger(CLIApplication.class.getCanonicalName())
                    .log(Level.SEVERE, "Unable to setup CLI application", e);
        }
    }

}

