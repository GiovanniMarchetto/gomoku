package it.units.sdm.gomoku.ui.cli;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class CLIApplication {

    // TODO : correct to declare here this variable?
    @NotNull
    public static final String newMatchAfterSummaryPropertyName = "newMatchAfterSummary"; // TODO : this variable is duplicated somewhere
    @NotNull
    public static final String continueAfterSummaryPropertyName = "continueAfterSummary";
    @NotNull
    public static final String extraGameAfterSummaryPropertyName = "extraGameAfterSummary";

    public static void launch() {
        try {
            new CLIMainView(new CLIMainViewmodel());
        } catch (IOException e) {
            Logger.getLogger(CLIApplication.class.getCanonicalName())
                    .log(Level.SEVERE, "Unable to setup CLI application", e);
        }
    }

}

