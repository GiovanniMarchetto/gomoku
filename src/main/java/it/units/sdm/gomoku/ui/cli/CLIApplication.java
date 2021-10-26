package it.units.sdm.gomoku.ui.cli;

import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import it.units.sdm.gomoku.mvvm_library.views.View;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class CLIApplication {

    // TODO : correct to declare here this variable?
    @NotNull
    public static final String newMatchAfterSummaryPropertyName = "newMatchAfterSummary";
    @NotNull
    public static final String continueAfterSummaryPropertyName = "continueAfterSummary";
    @NotNull
    public static final String extraGameAfterSummaryPropertyName = "extraGameAfterSummary";

    @NotNull
    private final View cliView;
    private final CLIMainViewModel cliViewmodel;

    private CLIApplication() throws IOException {
        this.cliView = new MainCLIView(new CLIMainViewModel());
        this.cliView.firePropertyChange(Setup.setupCompletedPropertyName, null, new CLISetup());
        this.cliViewmodel = ((CLIMainViewModel) cliView.getViewmodelAssociatedWithView());
    }

    public static void launch() {
        boolean newMatch;
        do {
            try {
                new CLIApplication().disputeMatch();
            } catch (IOException e) {
                Logger.getLogger(CLIApplication.class.getCanonicalName())
                        .log(Level.SEVERE, "Unable to setup CLI application", e);
            }
            System.out.print("Another match? Y/N: ");
            newMatch = Utility.getLowercaseCharIfValidCaseInsensitiveOr0('y', 'n') == 'y';
        } while (newMatch);
    }
    //
//    private static boolean newMatch(@NotNull final View mainCLIView) {
//
//    }

    private void disputeMatch() {   // TODO : move to CLIMainViewModel
        while (!cliViewmodel.isMatchEnded()) {
            System.out.println("\n\nNew game!");
            final int NUMBER_OF_PLAYERS = 2;    // TODO : magic number?
            while (!cliViewmodel.isCurrentGameEnded()) {
                for (int playerCounter = 0;
                     !cliViewmodel.isCurrentGameEnded() && playerCounter < NUMBER_OF_PLAYERS;
                     playerCounter++) {
                    System.out.println(cliViewmodel.getCurrentBoardAsString());
                    System.out.println("Turn of " + cliViewmodel.getCurrentPlayer());   // TODO : not showed for CPU player
                    cliViewmodel.waitForAValidMoveOfAPlayerAndPlaceStone();
                }
            }

            // TODO : i) summary of game/match not printed, ii) draw not handled, iii) "another match?" not handled

        }
    }
}

class MainCLIView extends View {
    public MainCLIView(Viewmodel viewmodelAssociatedWithView) {
        super(viewmodelAssociatedWithView);
    }
}