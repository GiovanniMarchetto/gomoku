package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.exceptions.CellAlreadyOccupiedException;
import it.units.sdm.gomoku.model.exceptions.CellOutOfBoardException;
import it.units.sdm.gomoku.model.exceptions.GameEndedException;
import org.jetbrains.annotations.NotNull;

public class HumanPlayer extends Player {

    public HumanPlayer(@NotNull String name) {
        super(name);
    }

    @Override
    public void makeMove() throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        setCoordinatesRequired(true);
        try {
            super.makeMove();
        } finally {
            setCoordinatesRequired(false);
        }
    }
}
