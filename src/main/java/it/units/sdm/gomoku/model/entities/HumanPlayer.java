package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.exceptions.BoardIsFullException;
import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import org.jetbrains.annotations.NotNull;

public class HumanPlayer extends Player {
    // TODO: TO BE TESTED

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove() throws BoardIsFullException, GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {  // TODO: test + refactor
        setCoordinatesRequired(true);
        try {
            super.makeMove();
        } catch (BoardIsFullException | GameEndedException | CellOutOfBoardException | CellAlreadyOccupiedException e) {
            throw e;
        } finally {
            setCoordinatesRequired(false);
        }
    }

}
