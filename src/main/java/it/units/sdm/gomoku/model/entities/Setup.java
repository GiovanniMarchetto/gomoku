package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Setup(Player player1,
                    Player player2,
                    PositiveInteger numberOfGames,
                    PositiveInteger boardSize) {

    public Setup(@NotNull final Player player1,
                 @NotNull final Player player2,
                 @NotNull final PositiveInteger numberOfGames,
                 @NotNull final PositiveInteger boardSize) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
        this.boardSize = Objects.requireNonNull(boardSize);
    }
}