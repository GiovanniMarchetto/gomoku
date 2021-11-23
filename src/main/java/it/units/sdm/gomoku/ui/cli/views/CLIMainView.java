package it.units.sdm.gomoku.ui.cli.views;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Game;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import it.units.sdm.gomoku.mvvm_library.View;
import it.units.sdm.gomoku.ui.cli.IOUtility;
import it.units.sdm.gomoku.ui.cli.viewmodels.CLIMainViewmodel;
import it.units.sdm.gomoku.utils.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CLIMainView extends View<CLIMainViewmodel> {

    public CLIMainView(@NotNull final CLIMainViewmodel cliMainViewmodel) {
        super(cliMainViewmodel);
        addObservedPropertyOfViewmodel(cliMainViewmodel.getCurrentGameStatusProperty(), evt -> {
            if (evt.getNewValue() == Game.Status.STARTED) {
                System.out.println("\n\nNew game!");
            }
        });

        addObservedPropertyOfViewmodel(cliMainViewmodel.getUserMustPlaceNewStoneProperty(), evt -> {
            if ((boolean) evt.getNewValue()) {
                try {
                    waitForAMoveOfAPlayer();
                } catch (GameEndedException e) {
                    Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Game is ended but should not", e);
                    throw new IllegalStateException(e);
                }
            }
        });

        addObservedPropertyOfViewmodel(cliMainViewmodel.getLastMoveCoordinatesProperty(), evt ->
                System.out.println("Move just done: " + evt.getNewValue() + System.lineSeparator()));

        addObservedPropertyOfViewmodel(cliMainViewmodel.getCurrentPlayerProperty(), evt -> {
            System.out.println(cliMainViewmodel.getCurrentBoardAsString());
            System.out.println("Turn of " + evt.getNewValue());
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

    private void waitForAMoveOfAPlayer() throws GameEndedException {
        CLIMainViewmodel viewmodel = getViewmodelAssociatedWithView();

        System.out.println("Insert next move:");
        Coordinates coordInsertedByTheUser = null;

        boolean invalidMove = true;
        while (invalidMove) {
            try {
                System.out.print("\tRow coordinate: ");
                int rowCoord = IOUtility.getAIntFromStdIn();

                System.out.print("\tColumn coordinate: ");
                int colCoord = IOUtility.getAIntFromStdIn();

                coordInsertedByTheUser = new Coordinates(rowCoord, colCoord);
                viewmodel.placeStoneFromUser(coordInsertedByTheUser);
                invalidMove = false;
            } catch (CellOutOfBoardException e) {
                System.out.println("Valid coordinates values are between " + 0 +
                        " and " + (viewmodel.getBoardSize() - 1) + " included.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid coordinates value.");
            } catch (CellAlreadyOccupiedException e) {
                System.out.println("The position " + coordInsertedByTheUser + " is already occupied.");
            } catch (GameEndedException e) {
                Logger.getLogger(getClass().getCanonicalName())
                        .log(Level.SEVERE, "Should never happen: the game is already ended", e);
                throw e;
            }
        }
    }
}
