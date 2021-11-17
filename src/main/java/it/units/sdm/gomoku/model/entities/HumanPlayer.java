package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.exceptions.*;
import org.jetbrains.annotations.NotNull;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove() throws NoGameSetException, BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {  // TODO: test + refactor
        setCoordinatesRequired(true);
        try {
            super.makeMove();
        } catch (NoGameSetException | BoardIsFullException | GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException e) {
            throw e;
        } finally {
            setCoordinatesRequired(false);
        }
    }

}
