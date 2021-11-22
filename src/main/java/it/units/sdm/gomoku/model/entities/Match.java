package it.units.sdm.gomoku.model.entities;

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
    private final List<Game> gameList;
    @NotNull
    private final PositiveInteger boardSize;
    @NotNull
    private final PositiveInteger totalNumberOfGames;
    @NotNull
    private Player currentBlackPlayer;
    @NotNull
    private Player currentWhitePlayer;

    public Match(@NotNull final Player player1, @NotNull final Player player2,
                 @NotNull final PositiveInteger numberOfGames, @NotNull final PositiveInteger boardSize) {
        this.currentBlackPlayer = Objects.requireNonNull(player1);
        this.currentWhitePlayer = Objects.requireNonNull(player2);
        this.gameList = new ArrayList<>();
        this.boardSize = Objects.requireNonNull(boardSize);
        this.totalNumberOfGames = Objects.requireNonNull(numberOfGames);
    }

    public Match(@NotNull final Setup setup) {  // TODO: test
        this(setup.player1(), setup.player2(), setup.numberOfGames(), Objects.requireNonNull(setup).boardSize());
    }

    public void incrementTotalNumberOfGames() {
        totalNumberOfGames.incrementAndGet();
    }

    @NotNull
    public Game initializeNewGame() throws MatchEndedException, GameNotEndedException {
        if (isEnded()) {
            throw new MatchEndedException();
        }

        if (isCurrentGameOngoing()) {
            throw new GameNotEndedException();
        }

        if (gameList.size() > 0) {
            invertPlayersColors();
        }

        Game newGame = new Game(boardSize, currentBlackPlayer, currentWhitePlayer);
        gameList.add(newGame);
        Stream.of(currentBlackPlayer, currentWhitePlayer)
                .forEach(player -> player.setCurrentGame(newGame));
        return newGame;
    }

    private void invertPlayersColors() {
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
    public int getTotalNumberOfGames() {
        return totalNumberOfGames.intValue();
    }

    @NotNull
    public Player getCurrentBlackPlayer() {
        return currentBlackPlayer;
    }

    @NotNull
    public Player getCurrentWhitePlayer() {
        return currentWhitePlayer;
    }

    @Nullable
    private Game getCurrentGame() {
        int numberOfGames = gameList.size();
        if (numberOfGames == 0) return null;
        return gameList.get(numberOfGames - 1);
    }

    private boolean isCurrentGameOngoing() {
        Game currentGame = getCurrentGame();
        return currentGame != null && !currentGame.isEnded();
    }

    public boolean isEnded() {
        return !isCurrentGameOngoing() && gameList.size() >= getTotalNumberOfGames();
    }

    public boolean isADraw() throws MatchNotEndedException {
        if (isEnded()) {
            return getScoreOfPlayer(getCurrentBlackPlayer())
                    .equals(getScoreOfPlayer(getCurrentWhitePlayer()));
        } else {
            throw new MatchNotEndedException();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        if (!gameList.equals(match.gameList)) return false;
        if (!boardSize.equals(match.boardSize)) return false;
        if (!totalNumberOfGames.equals(match.totalNumberOfGames)) return false;
        if (!currentBlackPlayer.equals(match.currentBlackPlayer)) return false;
        return currentWhitePlayer.equals(match.currentWhitePlayer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameList, boardSize, totalNumberOfGames, currentBlackPlayer, currentWhitePlayer);
    }
}
