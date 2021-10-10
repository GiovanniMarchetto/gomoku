package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.custom_types.PositiveInteger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static it.units.sdm.gomoku.custom_types.PositiveInteger.PositiveIntegerType;

public class Match {

    @NotNull
    private final static PositiveInteger DEFAULT_MAXIMUM_GAMES = new PositiveInteger(1);

    @NotNull
    private final List<Game> gameList;

    @NotNull
    private final PositiveInteger boardSize;

    @NotNull
    private PositiveInteger howManyGames;

    @NotNull
    private Player currentBlackPlayer,
            currentWhitePlayer;

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger howManyGames) {
        this.currentBlackPlayer = Objects.requireNonNull(player2);
        this.currentWhitePlayer = Objects.requireNonNull(player1);
        this.gameList = new ArrayList<>();
        this.boardSize = Objects.requireNonNull(boardSize);
        this.howManyGames = Objects.requireNonNull(howManyGames);
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @PositiveIntegerType int boardSize, @PositiveIntegerType int howManyGames) {
        this(player1, player2, new PositiveInteger(boardSize), new PositiveInteger(howManyGames));
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @PositiveIntegerType int boardSize) {
        this(player1, player2, boardSize, DEFAULT_MAXIMUM_GAMES.intValue());
    }

    @NotNull
    public Game startNewGame() {
        invertCurrentPlayersColors();
        Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
        gameList.add(newGame);
        return newGame;
    }

    private void invertCurrentPlayersColors() {
        Player oldWhitePlayer = currentWhitePlayer;
        currentWhitePlayer = currentBlackPlayer;
        currentBlackPlayer = oldWhitePlayer;
    }

    @NotNull
    public Map<Player, NonNegativeInteger> getScore() {
        Map<Player, NonNegativeInteger> score = new HashMap<>(2);
        score.put(currentBlackPlayer, getScoreOfPlayer(currentBlackPlayer));
        score.put(currentWhitePlayer, getScoreOfPlayer(currentWhitePlayer));
        return score;
    }

    @NotNull
    private NonNegativeInteger getScoreOfPlayer(@NotNull Player player) {
        return new NonNegativeInteger(
                (int) gameList.stream()
                        .filter(aGame -> {
                            try {
                                return aGame.getWinner() == Objects.requireNonNull(player);
                            } catch (Game.NotEndedGameException e) {
                                return false;
                            }
                        })
                        .count()
        );
    }
}