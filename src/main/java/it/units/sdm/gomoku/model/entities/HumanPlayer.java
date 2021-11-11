package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
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
        Utility.runOnSeparateThread(() -> {
            this.currentGame = Objects.requireNonNull(currentGame);
            setCoordinatesRequired(true);
        });
    }

    public void placeStone(@NotNull Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException, Game.GameEndedException {
        Objects.requireNonNull(coordinates);
        setCoordinatesRequired(false);
        try {
            Objects.requireNonNull(currentGame).placeStoneAndChangeTurn(coordinates);
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | RuntimeException e) {
            Utility.runOnSeparateThread(() -> setCoordinatesRequired(true));
            throw e;
        }
    }
}
