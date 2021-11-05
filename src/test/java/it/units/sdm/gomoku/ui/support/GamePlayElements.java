package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.entities.Board;
import it.units.sdm.gomoku.model.entities.Stone;

import java.util.List;

public record GamePlayElements(
        Stone[][] matrix,
        Coordinates coordinatesToControl,
        boolean isWinChainFromCoordinates,
        boolean isFinishedGame
) {
}