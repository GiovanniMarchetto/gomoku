package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    @Nullable
    private Game currentGame;

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove(@NotNull final Game currentGame) {
        this.currentGame = Objects.requireNonNull(currentGame);
        setCoordinatesRequired(true);
    }

    public void placeStone(@NotNull Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException {
        Objects.requireNonNull(coordinates);
        setCoordinatesRequired(false);
        Objects.requireNonNull(currentGame).placeStoneAndChangeTurn(coordinates);
    }
}
