package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.Length;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public abstract class Setup {       // TODO : to be tested

    public static final String setupCompletedPropertyName = "setupReady";

    @NotNull
    protected final Map<Player, PlayerTypes> players;

    @NotNull
    protected final PositiveInteger numberOfGames;

    @NotNull
    protected final PositiveOddInteger boardSize;

    protected Setup(@NotNull final Map<Player, PlayerTypes> players,
                    @NotNull final PositiveInteger numberOfGames,
                    @NotNull final PositiveOddInteger boardSizes) {
        this.players = Objects.requireNonNull(players);
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
        this.boardSize = Objects.requireNonNull(boardSizes);
    }

    @NotNull
    @Length(length = 2)
    public Player[] getPlayers() {
        return players.keySet().toArray(new Player[0]);
    }

    @NotNull
    public PositiveInteger getNumberOfGames() {
        return numberOfGames;
    }

    @NotNull
    public PositiveOddInteger getBoardSizeValue() {
        return boardSize;
    }

    @Override
    public String toString() {
        return "Setup{" +
                "players=" + players +
                ", numberOfGames=" + numberOfGames +
                ", boardSize=" + boardSize +
                '}';
    }

}
