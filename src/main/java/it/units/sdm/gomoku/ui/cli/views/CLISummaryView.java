package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.model.exceptions.GameNotEndedException;
import it.units.sdm.gomoku.model.exceptions.MatchNotEndedException;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import org.jetbrains.annotations.NotNull;

public class CLISummaryView extends View<CLIMainViewmodel> {

    public CLISummaryView(@NotNull CLIMainViewmodel cliMainViewmodel) {
        super(cliMainViewmodel);
    }

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();

        System.out.println("Game ended\n");

        CLIMainViewmodel viewmodel = getViewmodelAssociatedWithView();
        System.out.println(viewmodel.getCurrentGame());
        try {
            Player winnerOfGame = viewmodel.getWinnerOfTheGame();
            System.out.println("The game is ended with: " +
                    (winnerOfGame != null ? "WIN of " + winnerOfGame : "a DRAW"));
        } catch (GameNotEndedException ignored) {
        }

        System.out.println("\nThe match score now is:\n" + viewmodel.getScoreOfMatch());

        if (viewmodel.isMatchEnded()) {
            try {
                if (viewmodel.isMatchEndedWithADraw()) {
                    System.out.print("Extra game? Y/N: ");
                    if (IOUtility.isYesFromStdin()) {
                        viewmodel.startExtraGame();
                    }
                }
            } catch (MatchNotEndedException ignored) {
            }

            if (viewmodel.isMatchEnded()) {
                Player winnerOfMatch = null;
                try {
                    winnerOfMatch = viewmodel.getWinnerOfTheMatch();
                } catch (MatchNotEndedException ignored) {
                }
                System.out.println("The match is finish with: " +
                        (winnerOfMatch != null ? "WIN of" + winnerOfMatch : "DRAW"));

                System.out.print("Another match? Y/N: ");
                if (IOUtility.isYesFromStdin()) {
                    viewmodel.startNewMatch();
                }
            }
        } else {
            viewmodel.initializeNewGame();
        }
    }
}
