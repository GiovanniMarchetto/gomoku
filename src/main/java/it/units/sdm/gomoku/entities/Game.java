package it.units.sdm.gomoku.entities;

import it.units.sdm.gomoku.custom_types.Coordinates;
import it.units.sdm.gomoku.custom_types.PositiveInteger;
import it.units.sdm.gomoku.custom_types.PositiveOddInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Game implements Comparable<Game> {

    @NotNull
    public static final PositiveInteger NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING = new PositiveOddInteger(5);
    @NotNull
    private final Board board;
    @NotNull
    private final Instant start;
    @NotNull
    private final Map<Player, Board.Stone> playerToStoneColorsMap;
    @Nullable
    private Player winner;  // available after the end of the game

    public Game(@NotNull PositiveInteger boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this.board = new Board(Objects.requireNonNull(boardSize));
        playerToStoneColorsMap = new ConcurrentHashMap<>();
        playerToStoneColorsMap.put(Objects.requireNonNull(blackPlayer), Board.Stone.BLACK);
        playerToStoneColorsMap.put(Objects.requireNonNull(whitePlayer), Board.Stone.WHITE);
        this.start = Instant.now();
    }

    public Game(int boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this(new PositiveInteger(boardSize), blackPlayer, whitePlayer);
    }


    @NotNull
    public Board getBoard() {
        return board;
    }

    public void placeStone(@NotNull final Player player, @NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        board.occupyPosition(
                playerToStoneColorsMap.get(Objects.requireNonNull(player)),
                Objects.requireNonNull(coordinates)
        );
        setWinnerIfPlayerWon(player, coordinates);
    }

    private void setWinnerIfPlayerWon(@NotNull Player player, @NotNull Coordinates coordinates) {
        if (hasThePlayerWonThisGame(coordinates)) {
            setWinner(player);
        }
    }

    private boolean hasThePlayerWonThisGame(@NotNull final Coordinates lastMove) {
        return Board.checkNConsecutiveStonesNaive(board, lastMove, NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING);
    }

    public Player getWinner() throws NotEndedGameException {
        if (isThisGameEnded()) {
            return winner;
        } else {
            throw new NotEndedGameException();
        }
    }

    private void setWinner(@NotNull final Player winner) {
        this.winner = Objects.requireNonNull(winner);
    }

    public boolean isThisGameEnded() {
        return winner != null || !board.isAnyEmptyPositionOnTheBoard();
        // if there are empty positions on the board it can't be a draw
    }

    @Override
    public int compareTo(@NotNull Game other) {
        return this.start.compareTo(other.start);
    }

    @Override
    public String toString() {
        return "Game started at " + start + "\n" +
                playerToStoneColorsMap + "\n" +
                "Winner: " + winner + "\n" +
                board;
    }

    public static class NotEndedGameException extends Exception {
        public NotEndedGameException() {
            super("The game is not over.");
        }
    }

}
