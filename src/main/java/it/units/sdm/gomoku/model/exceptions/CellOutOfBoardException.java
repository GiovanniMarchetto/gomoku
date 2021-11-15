package it.units.sdm.gomoku.model.exceptions;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CellOutOfBoardException extends Exception {
    public CellOutOfBoardException(@NotNull final Coordinates invalidCoords) {
        super(Objects.requireNonNull(invalidCoords) + " is out of board.");
    }
}
