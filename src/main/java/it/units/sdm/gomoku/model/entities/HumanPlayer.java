package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Utility;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove(@NotNull final Game currentGame) {  // TODO: test + refactor
        Utility.runOnSeparateThread(() -> {
            Objects.requireNonNull(currentGame);
            setCoordinatesRequired(true);
            super.makeMove(currentGame);
            setCoordinatesRequired(false);
        });
    }

}
