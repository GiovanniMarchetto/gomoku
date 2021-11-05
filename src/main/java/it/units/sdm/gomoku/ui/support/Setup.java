package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.entities.Player;

public record Setup(
        Player player1,
        Player player2,
        PositiveInteger numberOfGames,
        PositiveInteger boardSize
) {
}