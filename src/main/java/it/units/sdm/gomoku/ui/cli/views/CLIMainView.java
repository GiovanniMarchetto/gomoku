package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.model.entities.Player;
import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMainView extends View<CLIMainViewmodel> implements Observer {    // TODO : all events clutter the memory stack
    // TODO : refactor: a lot in common with view of GUI (e.g. property events), may refactor?
    // TODO : test

    public CLIMainView(@NotNull final CLIMainViewmodel cliMainViewmodel) {  // TODO : event-based flux of program to be tested
        super(cliMainViewmodel);
        addObservedPropertyOfViewmodel(cliMainViewmodel.getCurrentGameStatusProperty(), evt -> {
            switch ((Game.Status) evt.getNewValue()) {
                case STARTED -> System.out.println("\n\nNew game!");
                case ENDED -> {
                    System.out.println("Game ended\n");

                    CLIMainViewmodel viewmodel = getViewmodelAssociatedWithView();
                    try {
                        Player winnerOfGame = viewmodel.getWinnerOfTheGame();
                        System.out.println("The game is finish with: " +
                                (winnerOfGame != null ? "WIN of " + winnerOfGame : "a DRAW"));
                    } catch (Game.GameNotEndedException ignored) {
                    }

                    System.out.println("\nThe match score now is:\n" + viewmodel.getScoreOfMatch());

                    if (viewmodel.isMatchEnded()) {
                        try {
                            if (viewmodel.isMatchEndedWithADraw()) {   // TODO: replace all occurrences of "*draw*" with "*draw*" (remember: don't search only entire word matching)
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
            }
        });
        addObservedPropertyOfViewmodel(cliMainViewmodel.getUserMustPlaceNewStoneProperty(), evt -> {
            if ((boolean) evt.getNewValue()) {
                try {
                    waitForAValidMoveOfAPlayer();
                } catch (Board.BoardIsFullException | Game.GameEndedException e) {
                    // TODO : handle exception
                    System.err.println("Game terminated due to an unexpected exception: ");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        });
        addObservedPropertyOfViewmodel(cliMainViewmodel.getLastMoveCoordinatesProperty(), evt -> {
            // TODO : update the view according to the last move?
        });
        addObservedPropertyOfViewmodel(cliMainViewmodel.getCurrentPlayerProperty(), evt -> {
            // TODO : update the view, e.g.: "Turn of ${currentPlayer}"
        });
    }

    @Override
    public void onViewInitialized() {
        super.onViewInitialized();
        getViewmodelAssociatedWithView().triggerFirstMove();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    private void waitForAValidMoveOfAPlayer() throws Board.BoardIsFullException, Game.GameEndedException {  // TODO : not tested
        int rowCoord, colCoord;
        boolean validMove = false;

        CLIMainViewmodel viewmodel = getViewmodelAssociatedWithView();

        System.out.println(viewmodel.getCurrentBoardAsString());
        System.out.println("Turn of " + viewmodel.getCurrentPlayer());   // TODO : not showed for CPU player

        System.out.println("Insert next move:");
        Coordinates coordInsertedByTheUser = null;
        do {
            try {
                System.out.print("\tRow coordinate: ");
                rowCoord = IOUtility.getAIntFromStdIn();
                System.out.print("\tColumn coordinate: ");
                colCoord = IOUtility.getAIntFromStdIn();
                coordInsertedByTheUser = new Coordinates(rowCoord, colCoord);
                viewmodel.placeStoneFromUser(coordInsertedByTheUser);  // TODO : correct or stones should be placed by the viewmodel?
                validMove = true;
            } catch (IndexOutOfBoundsException e) {
                System.out.print("Valid coordinates values are between " + 0 +
                        " and " + (viewmodel.getBoardSize() - 1) + " included.");
            } catch (IllegalArgumentException e) {
                System.out.print("Invalid coordinates value.");
            } catch (Board.BoardIsFullException e) {
                Logger.getLogger(getClass().getCanonicalName())
                        .log(Level.SEVERE, "Should never happen: no more empty position" +
                                " available on the board but game should be already ended", e);
                throw e;
            } catch (Board.CellAlreadyOccupiedException e) {
                System.out.print("The position " + coordInsertedByTheUser + " is already occupied.");
            } catch (Game.GameEndedException e) {
                Logger.getLogger(getClass().getCanonicalName())
                        .log(Level.SEVERE, "Should never happen: the game is already ended", e);
                throw e;
            }
        } while (!validMove);
    }

}
