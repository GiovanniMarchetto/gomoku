package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.entities.Match;
import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.mvvm_library.Viewmodel;
import it.units.sdm.gomoku.ui.AbstractMainViewmodel;
import it.units.sdm.gomoku.ui.cli.CLIAbstractSetup;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;

import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMainView extends View implements Observer {    // TODO : all events clutter the memory stack

    public CLIMainView(Viewmodel viewmodelAssociatedWithView) throws IOException {  // TODO : event-based flux of program to be tested
        super(viewmodelAssociatedWithView);
        getViewmodelAssociatedWithView().addPropertyChangeListener(this);// TODO : rethink about this (should View abstract class implement Observer to observer its corresponding Viewmodel)
        ((CLIMainViewmodel) viewmodelAssociatedWithView).createMatchFromSetupAndStartGame(new CLIAbstractSetup());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        switch (evt.getPropertyName()) {
            case Game.newGameStartedPropertyName -> {
                if ((boolean) evt.getNewValue()) {
                    System.out.println("\n\nNew game!");
                }
            }
            case AbstractMainViewmodel.userMustPlaceNewStonePropertyName -> {
                if ((boolean) evt.getNewValue()) {
                    try {
                        waitForAValidMoveOfAPlayer();
                    } catch (Board.NoMoreEmptyPositionAvailableException e) {
                        // TODO : handle exception
                        System.err.println("Game terminated due an unexpected exception: ");
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
            case Game.isThisGameEndedPropertyName -> {
                if ((boolean) evt.getNewValue()) {
                    System.out.println("Game ended");
                    // TODO : print summary
                    CLIMainViewmodel viewmodel = (CLIMainViewmodel) getViewmodelAssociatedWithView();
                    if (viewmodel.isMatchEnded()) {

                        boolean isMatchEndedWithDraft = false;
                        try {
                            isMatchEndedWithDraft = viewmodel.getWinnerOfTheMatch() == null;  // TODO : viewmodel should have a method "isMatchEndedWithADraft()" to avoid "==null"
                        } catch (Match.MatchNotEndedException /* TODO: should be MatchNotEndedException */ e) {
                            Logger.getLogger(getClass().getCanonicalName())
                                    .log(Level.SEVERE, "Impossible to be here", e);
                        }

                        if (isMatchEndedWithDraft) {
                            System.out.print("Extra game? Y/N: ");    // TODO: refactor with lines down?
                            boolean anotherGame = IOUtility.getLowercaseCharWhenValidCaseInsensitiveOrCycle('y', 'n') == 'y';
                            if (anotherGame) {
                                viewmodel.startExtraGame();
                            }
                        } else {
                            System.out.print("Another match? Y/N: ");
                            boolean anotherMatch = IOUtility.getLowercaseCharWhenValidCaseInsensitiveOrCycle('y', 'n') == 'y';
                            if (anotherMatch) {
                                viewmodel.startNewMatch();
                            }
                        }
                    } else {
                        viewmodel.startNewGame();
                    }
                }
            }
        }
    }

    private void waitForAValidMoveOfAPlayer() throws Board.NoMoreEmptyPositionAvailableException {  // TODO : not tested
        int rowCoord = 0, colCoord = 0;
        boolean validMove = false;

        AbstractMainViewmodel viewmodel =
                ((AbstractMainViewmodel) getViewmodelAssociatedWithView());

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
            } catch (Board.NoMoreEmptyPositionAvailableException e) {
                Logger.getLogger(getClass().getCanonicalName())
                        .log(Level.SEVERE, "Should never happen: no more empty position" +
                                " available on the board but game should be already ended", e);
                throw e;
            } catch (Board.PositionAlreadyOccupiedException e) {
                System.out.print("The position " + coordInsertedByTheUser + " is already occupied.");
            }
        } while (!validMove);
    }

}
