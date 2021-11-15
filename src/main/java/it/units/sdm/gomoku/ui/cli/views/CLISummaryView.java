package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.actors.Player;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;

public class CLISummaryView extends View<CLIMainViewmodel> implements Observer {

    public CLISummaryView(@NotNull CLIMainViewmodel cliMainViewmodel) {
        super(cliMainViewmodel);
    }

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();

        System.out.println("Game ended\n");

        CLIMainViewmodel viewmodel = getViewmodelAssociatedWithView();
        try {
            Player winnerOfGame = viewmodel.getWinnerOfTheGame();
            System.out.println("The game is ended with: " +
                    (winnerOfGame != null ? "WIN of " + winnerOfGame : "a DRAW"));
        } catch (Game.GameNotEndedException ignored) {
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
            } catch (Match.MatchNotEndedException ignored) {
            }

            if (viewmodel.isMatchEnded()) {
                Player winnerOfMatch = null;
                try {
                    winnerOfMatch = viewmodel.getWinnerOfTheMatch();
                } catch (Match.MatchNotEndedException ignored) {
                }
                System.out.println("The match is finish with: " +
                        (winnerOfMatch != null ? "WIN of" + winnerOfMatch : "DRAW"));

                System.out.print("Another match? Y/N: ");
                if (IOUtility.isYesFromStdin()) {
                    viewmodel.startNewMatch();
                }
            }
        } else {
            viewmodel.startNewGame();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }
}
