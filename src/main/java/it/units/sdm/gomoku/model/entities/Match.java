package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.Length;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.NonNegativeInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
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
    private Player currentBlackPlayer,
            currentWhitePlayer;

    public Match(@NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger numberOfGames,
                 @NotNull @Length(length = 2) final Player... players) {
        this(validatePlayersFromVarargs(players)[0], players[1],
                Objects.requireNonNull(boardSize), Objects.requireNonNull(numberOfGames));
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @NotNull final PositiveInteger boardSize, @NotNull final PositiveInteger numberOfGames) {
        this.currentBlackPlayer = Objects.requireNonNull(player2);
        this.currentWhitePlayer = Objects.requireNonNull(player1);
        this.gameList = new ArrayList<>();
        this.boardSize = Objects.requireNonNull(boardSize);
        this.numberOfGames = Objects.requireNonNull(numberOfGames);
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @PositiveIntegerType int boardSize, @PositiveIntegerType int numberOfGames) {
        this(player1, player2, new PositiveInteger(boardSize), new PositiveInteger(numberOfGames));
    }

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @PositiveIntegerType int boardSize) {
        this(player1, player2, boardSize, DEFAULT_MAXIMUM_GAMES.intValue());
    }

    @NotNull
    @Length(length = 2)
    private static Player[] validatePlayersFromVarargs(@NotNull final Player[] players) {
        if (Objects.requireNonNull(players).length != 2) {
            throw new IllegalArgumentException("2 players expected but " + players.length + " found.");
        }
        return players;
    }

    public static void executeMoveOfPlayerInGame(@NotNull Game game, @Nullable Coordinates coordinatesOfTheMove)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {

        assert coordinatesOfTheMove != null;
        game.placeNextStone(coordinatesOfTheMove);
    }

    public void addAnExtraGame() {
        numberOfGames.incrementAndGet();
    }


    @NotNull
    public Game startNewGame() throws MatchEndedException {
        if (!isEnded()) {
            invertCurrentPlayersColors();
            Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
            gameList.add(newGame);
            return newGame;
        } else {
            throw new MatchEndedException();
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
    public Player getWinner() throws MatchEndedException {
        if (isEnded()) {
            if (isEndedWithADraft()) {
                return null;
            } else {
                int scoreOfCurrentBlackPlayer = getScoreOfPlayer(currentBlackPlayer).intValue();
                int scoreOfCurrentWhitePlayer = getScoreOfPlayer(currentWhitePlayer).intValue();

                if (scoreOfCurrentBlackPlayer > scoreOfCurrentWhitePlayer) {
                    return currentBlackPlayer;
                } else {
                    return currentWhitePlayer;
                }
            }
        } else {
            throw new MatchEndedException();
        }
    }

    @NotNull
    public NonNegativeInteger getScoreOfPlayer(@NotNull Player player) {
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

    public boolean isEndedWithADraft() {
        return getScoreOfPlayer(getCurrentBlackPlayer())
                .equals(getScoreOfPlayer(getCurrentWhitePlayer()));
    }

    public boolean isEnded() {
        int numberOfGamesPlayed = gameList.size();
        if (numberOfGamesPlayed == 0) return false;
        return
                gameList.get(numberOfGamesPlayed - 1).isThisGameEnded()
                        && numberOfGamesPlayed >= getNumberOfGames();
    }

    public static class MatchEndedException extends Exception {
    }
}
