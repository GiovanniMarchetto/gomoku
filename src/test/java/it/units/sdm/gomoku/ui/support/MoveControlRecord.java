package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Cell;

public record MoveControlRecord(
        Cell[][] matrix,
        Coordinates coordinatesToControl,
        boolean isWinChainFromCoordinates,
        boolean isFinishedGame
) {
}