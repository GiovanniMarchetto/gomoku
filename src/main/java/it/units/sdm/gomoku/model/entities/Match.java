package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Length;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.ui.support.Setup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public Match(@NotNull final Setup setup) {
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

    @Deprecated
    public static void executeMoveOfPlayerInGame(@NotNull Game game, @NotNull Coordinates coordinatesOfTheMove)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException {
        game.placeStoneAndChangeTurn(coordinatesOfTheMove);
    }

    public void addAnExtraGame() {
        //TODO: need or startExtraGame with exception for lastGame not Ended?
        numberOfGames.incrementAndGet();
    }


    @NotNull
    public Game startNewGame() throws MatchEndedException, MaxNumberOfGamesException {
        if (gameList.size() < getNumberOfGames()) {
            if (!isEnded()) {
                //TODO: need exception for lastGame not Ended?
                if (gameList.size() > 0) {
                    invertCurrentPlayersColors();
                }
                Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
                gameList.add(newGame);
                return newGame;
            } else {
                throw new MatchEndedException();
            }
        } else {
            throw new MaxNumberOfGamesException();
        }
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
        if (isEnded()) {
            if (isADraft()) {
                return null;
            } else {
                return getScoreOfPlayer(currentBlackPlayer).compareTo(getScoreOfPlayer(currentWhitePlayer)) > 0
                        ? currentBlackPlayer
                        : currentWhitePlayer;
            }
        } else {
            throw new MatchNotEndedException();
        }
    }

    @NotNull
    public NonNegativeInteger getScoreOfPlayer(@NotNull Player player) {
        return new NonNegativeInteger(
                (int) gameList.stream()
                        .filter(aGame -> {
                            try {
                                return aGame.getWinner() == Objects.requireNonNull(player);
                            } catch (Game.GameNotEndedException e) {
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

    public boolean isADraft() {
        return isEnded() && getScoreOfPlayer(getCurrentBlackPlayer())
                .equals(getScoreOfPlayer(getCurrentWhitePlayer()));
    }

    public boolean isEnded() {
        int numberOfGamesPlayed = gameList.size();
        if (numberOfGamesPlayed == 0) return false;
        return gameList.get(numberOfGamesPlayed - 1).isThisGameEnded()
                && numberOfGamesPlayed >= getNumberOfGames();
    }

    public static class MatchEndedException extends Exception {
    }

    public static class MatchNotEndedException extends Exception {
    }

    public static class MaxNumberOfGamesException extends Exception {

    }
}
