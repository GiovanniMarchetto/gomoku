package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    public static final String coordinatesRequiredToContinuePropertyName = "coordinatesRequiredToContinue";

    private boolean coordinatesRequiredToContinue = false;

    @Nullable
    private Game currentGame;

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove(@NotNull final Game currentGame) {
        this.currentGame = Objects.requireNonNull(currentGame);
        setCoordinatesRequiredToContinue(true);
    }

    public void placeStone(@NotNull Coordinates coordinates) throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException {
        Objects.requireNonNull(coordinates);
        setCoordinatesRequiredToContinue(false);
        Objects.requireNonNull(currentGame).placeStoneAndChangeTurn(coordinates);
    }

    public boolean isCoordinatesRequiredToContinue() {
        return coordinatesRequiredToContinue;
    }

    public void setCoordinatesRequiredToContinue(boolean coordinatesRequiredToContinue) {
        var oldValue = this.coordinatesRequiredToContinue;
        if (coordinatesRequiredToContinue != oldValue) {
            this.coordinatesRequiredToContinue = coordinatesRequiredToContinue;
            firePropertyChange(coordinatesRequiredToContinuePropertyName, oldValue, coordinatesRequiredToContinue);
        }
    }
}
