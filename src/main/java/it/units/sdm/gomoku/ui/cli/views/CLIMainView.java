package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMainView extends View<CLIMainViewmodel> implements Observer {    // TODO : all events clutter the memory stack
    // TODO : refactor: a lot in common with view of GUI (e.g. property events), may refactor?
    // TODO : test
    private final @NotNull ZonedDateTime time;

    public CLIMainView(@NotNull final CLIMainViewmodel cliMainViewmodel) {  // TODO : event-based flux of program to be tested
        super(cliMainViewmodel);
        time = cliMainViewmodel.getGameStartTime();
        addObservedPropertyOfViewmodel(cliMainViewmodel.getCurrentGameStatusProperty(), evt -> {
            if (evt.getNewValue() == Game.Status.STARTED && time.equals(cliMainViewmodel.getGameStartTime())) {
                System.out.println("\n\nNew game!");
            }
        });
        addObservedPropertyOfViewmodel(cliMainViewmodel.getUserMustPlaceNewStoneProperty(), evt -> {
            if ((boolean) evt.getNewValue() && time.equals(cliMainViewmodel.getGameStartTime())) {
                try {
                    waitForAMoveOfAPlayer();
                } catch (BoardIsFullException | GameEndedException e) {
                    // TODO : handle exception
                    System.err.println("Game terminated due to an unexpected exception: ");
                    e.printStackTrace();    // TODO : use logger
                    System.exit(1);     // TODO : correct?
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
    public void onViewDisappearing() {
        super.onViewDisappearing();
        stopObservingAllViewModelProperties();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
    }

    private void waitForAMoveOfAPlayer() throws BoardIsFullException, GameEndedException {  // TODO : not tested
        int rowCoord, colCoord;

        CLIMainViewmodel viewmodel = getViewmodelAssociatedWithView();

        System.out.println(viewmodel.getCurrentBoardAsString());
        System.out.println("Turn of " + viewmodel.getCurrentPlayer());   // TODO : not showed for CPU player

        System.out.println("Insert next move:");
        Coordinates coordInsertedByTheUser = null;
        try {
            System.out.print("\tRow coordinate: ");
            rowCoord = IOUtility.getAIntFromStdIn();
            System.out.print("\tColumn coordinate: ");
            colCoord = IOUtility.getAIntFromStdIn();
            coordInsertedByTheUser = new Coordinates(rowCoord, colCoord);
            viewmodel.placeStoneFromUser(coordInsertedByTheUser);
        } catch (CellOutOfBoardException e) {
            System.out.println("Valid coordinates values are between " + 0 +
                    " and " + (viewmodel.getBoardSize() - 1) + " included.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid coordinates value.");
        } catch (BoardIsFullException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "Should never happen: no more empty position" +
                            " available on the board but game should be already ended", e);
            throw e;
        } catch (CellAlreadyOccupiedException e) {
            System.out.println("The position " + coordInsertedByTheUser + " is already occupied.");
        } catch (GameEndedException e) {
            Logger.getLogger(getClass().getCanonicalName())
                    .log(Level.SEVERE, "Should never happen: the game is already ended", e);
            throw e;
        }
    }

}
