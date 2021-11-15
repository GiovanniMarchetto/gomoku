package it.units.sdm.gomoku.model.exceptions;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CellAlreadyOccupiedException extends Exception {
    public CellAlreadyOccupiedException(@NotNull final Coordinates coordinates) {
        super(Objects.requireNonNull(coordinates) + " already occupied.");
    }
}
