package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Length;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.GameNotEndedException;
import it.units.sdm.gomoku.model.exceptions.MatchEndedException;
import it.units.sdm.gomoku.model.exceptions.MatchNotEndedException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.custom_types.PositiveInteger.PositiveIntegerType;

public class Match {

    @NotNull
    private final static PositiveInteger DEFAULT_MAXIMUM_GAMES = new PositiveInteger(1);
    @NotNull
    private final List<Game> gameList;
    @NotNull
    private final PositiveInteger boardSize;
    @NotNull
    private final PositiveInteger numberOfGames;
    @NotNull
    private Player currentBlackPlayer;
    @NotNull
    private Player currentWhitePlayer;

    // TODO : ctor may take Setup instance as input param?
    public Match(@NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger numberOfGames,
                 @NotNull @Length(length = 2) final Player... players) {
        // TODO : consider to use a builder instead of a constructor with so many params
        this(Objects.requireNonNull(boardSize), Objects.requireNonNull(numberOfGames),
                validatePlayersFromVarargs(players)[0], players[1]);
    }

    public Match(@NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger numberOfGames,
                 @NotNull final Player player1, @NotNull final Player player2) {
        this.currentBlackPlayer = Objects.requireNonNull(player1);
        this.currentWhitePlayer = Objects.requireNonNull(player2);
        this.gameList = new ArrayList<>();
        this.boardSize = Objects.requireNonNull(boardSize);
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
    }

    public Match(@PositiveIntegerType int boardSize, @PositiveIntegerType int numberOfGames,
                 @NotNull final Player player1, @NotNull final Player player2) {
        this(new PositiveInteger(boardSize), new PositiveInteger(numberOfGames), player1, player2);
    }

    public Match(@PositiveIntegerType int boardSize, @NotNull final Player player1, @NotNull final Player player2) {
        this(boardSize, DEFAULT_MAXIMUM_GAMES.intValue(), player1, player2);
    }

    public Match(@NotNull final Setup setup) {  // TODO: test
        this(Objects.requireNonNull(setup).boardSize(), setup.numberOfGames(), setup.player1(), setup.player2());
    }

    @NotNull
    @Length(length = 2)
    private static Player[] validatePlayersFromVarargs(@NotNull final Player[] players) {
        if (Objects.requireNonNull(players).length != 2) {
            throw new IllegalArgumentException("2 players expected but " + players.length + " found.");
        }
        return players;
    }

    public void addAnExtraGame() {
        numberOfGames.incrementAndGet();
    }


    @NotNull
    public Game initializeNewGame() throws MatchEndedException, GameNotEndedException {
//        if (!isCurrentGameEnded()) {//TODO: de-comment this and correct tests
//            throw new GameNotEndedException();
//        }

        if (isEnded()) {
            throw new MatchEndedException();
        }

        if (gameList.size() > 0) {
            invertCurrentPlayersColors();
        }
        Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
        gameList.add(newGame);
        Stream.of(currentBlackPlayer, currentWhitePlayer)
                .forEach(player -> player.setCurrentGame(newGame));
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

    @Nullable
    public Player getWinner() throws MatchNotEndedException {
        if (isADraw()) {
            return null;
        } else {
            return getScoreOfPlayer(currentBlackPlayer).compareTo(getScoreOfPlayer(currentWhitePlayer)) > 0
                    ? currentBlackPlayer
                    : currentWhitePlayer;
        }
    }

    @NotNull
    private NonNegativeInteger getScoreOfPlayer(@NotNull Player player) {
        return new NonNegativeInteger(
                (int) gameList.stream()
                        .filter(aGame -> {
                            try {
                                return aGame.getWinner() == Objects.requireNonNull(player);
                            } catch (GameNotEndedException e) {
                                return false;
                            }
                        })
                        .count()
        );
    }

    @PositiveIntegerType
    public int getNumberOfGames() {
        return numberOfGames.intValue();
    }

    @NotNull
    public Player getCurrentBlackPlayer() {
        return currentBlackPlayer;
    }

    @NotNull
    public Player getCurrentWhitePlayer() {
        return currentWhitePlayer;
    }

    public boolean isADraw() throws MatchNotEndedException {
        if (isEnded()) {
            return getScoreOfPlayer(getCurrentBlackPlayer())
                    .equals(getScoreOfPlayer(getCurrentWhitePlayer()));
        } else {
            throw new MatchNotEndedException();
        }
    }

    public boolean isEnded() {
        return isCurrentGameEnded()
                && gameList.size() >= getNumberOfGames();
    }

    private boolean isCurrentGameEnded() {
        final Game currentGame = getCurrentGame();
        return currentGame != null && currentGame.isEnded();
    }

    @Nullable
    private Game getCurrentGame() {
        int numberOfGamesPlayed = gameList.size();
        if (numberOfGamesPlayed == 0) return null;
        return gameList.get(numberOfGamesPlayed - 1);
    }

}
