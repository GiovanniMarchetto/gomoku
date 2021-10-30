package it.units.sdm.gomoku.ui.support;

import it.units.sdm.gomoku.Length;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import it.units.sdm.gomoku.model.entities.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public abstract class AbstractSetup {       // TODO : to be tested

    @NotNull
    protected final Player[] players;

    @NotNull
    protected final PositiveInteger numberOfGames;

    @NotNull
    protected final PositiveOddInteger boardSize;

    protected AbstractSetup(@NotNull final Player[] players,
                            @NotNull final PositiveInteger numberOfGames,
                            @NotNull final PositiveOddInteger boardSizes) {
        this.players = Objects.requireNonNull(players);
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
        this.boardSize = Objects.requireNonNull(boardSizes);
    }

    protected AbstractSetup(@NotNull final Player playerOne,
                            @NotNull final Player playerTwo,
                            @NotNull final PositiveInteger numberOfGames,
                            @NotNull final PositiveOddInteger boardSizes) {
        this(
                new Player[]{Objects.requireNonNull(playerOne), Objects.requireNonNull(playerTwo)},
                numberOfGames, boardSizes
        );
    }

    @NotNull
    @Length(length = 2)
    public Player[] getPlayers() {
        return players;
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
                "players=" + Arrays.toString(players) +
                ", numberOfGames=" + numberOfGames +
                ", boardSize=" + boardSize +
                '}';
    }

}
