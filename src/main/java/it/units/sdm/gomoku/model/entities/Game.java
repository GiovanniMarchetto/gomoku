package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.Observable;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.custom_types.PositiveOddInteger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

public class Game implements Comparable<Game>, Observable {

    @NotNull
    public static final PositiveInteger NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING = new PositiveOddInteger(5);
    @NotNull
    public static final String gameEndedPropertyName = "gameEnded";
    @NotNull
    private final Board board;
    @NotNull
    private final Instant start;
    @NotNull
    private final Player blackPlayer, whitePlayer;
    @NotNull
    private Player currentPlayer;
    @Nullable
    private Player winner;  // available after the end of the game

    public Game(@NotNull PositiveInteger boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this.board = new Board(Objects.requireNonNull(boardSize));
        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
        this.currentPlayer = blackPlayer;
        this.start = Instant.now();
    }

    public Game(int boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this(new PositiveInteger(boardSize), blackPlayer, whitePlayer);
    }

    @NotNull
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @NotNull
    public Board getBoard() {
        return board;
    }

    @NotNull
    private Board.Stone getStoneOfPlayer(@NotNull final Player player) {
        return player.equals(blackPlayer) ? Board.Stone.BLACK : Board.Stone.WHITE;
    }

    public void placeNextStone(@NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        placeStone(currentPlayer, coordinates);
        changeTurn();
    }

    private void placeStone(@NotNull final Player player, @NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        board.occupyPosition(getStoneOfPlayer(Objects.requireNonNull(player)), Objects.requireNonNull(coordinates));
        setWinnerIfPlayerWon(player, coordinates);
        if (isThisGameEnded()) {
            firePropertyChange(gameEndedPropertyName, false, true);
        }
    }

    private void changeTurn() {
        currentPlayer = currentPlayer == blackPlayer ? whitePlayer : blackPlayer;
    }

    private void setWinnerIfPlayerWon(@NotNull Player player, @NotNull Coordinates coordinates) {
        if (hasThePlayerWonThisGame(coordinates)) {
            setWinner(player);
        }
    }

    private boolean hasThePlayerWonThisGame(@NotNull final Coordinates lastMove) {
        return Board.checkNConsecutiveStonesNaive(board, lastMove, NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING);
    }

    @Nullable
    public Player getWinner() throws GameNotEndedException {
        if (isThisGameEnded()) {
            return winner;
        } else {
            throw new GameNotEndedException();
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
        return "Game started at " + start.atZone(ZoneId.systemDefault()) + "\n" +
                blackPlayer + " -> BLACK, " +
                whitePlayer + " -> WHITE" + "\n" +
                "Winner: " + winner + "\n" +
                board;
    }

    public static class GameNotEndedException extends Exception {
        public GameNotEndedException() {
            super("The game is not over.");
        }
    }

}
