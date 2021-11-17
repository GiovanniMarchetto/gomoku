package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.exceptions.NoGameSetException;
import org.jetbrains.annotations.NotNull;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove() throws NoGameSetException {  // TODO: test + refactor
        setCoordinatesRequired(true);
        super.makeMove();
        setCoordinatesRequired(false);
    }

}
