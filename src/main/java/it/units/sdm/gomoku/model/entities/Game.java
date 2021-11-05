package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class Game implements Comparable<Game>, Observable {

    @NotNull
    public static final PositiveInteger NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING = new PositiveInteger(5);
    @NotNull
    private final Board board;
    @NotNull
    private final Instant start;
    @NotNull
    private final Player blackPlayer, whitePlayer;
    @NotNull
    public final Property<Player> currentPlayer;    // TODO : property are public...
    @NotNull
    public final Property<Boolean> gameEnded;
    @NotNull
    public final Property<Boolean> newGameStarted;
    @NotNull
    private Player winner;  // available after the end of the game

    public Game(@NotNull PositiveInteger boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this.board = new Board(Objects.requireNonNull(boardSize));
        this.blackPlayer = Objects.requireNonNull(blackPlayer);
        this.whitePlayer = Objects.requireNonNull(whitePlayer);
        this.currentPlayer = new Property<>(blackPlayer, this);
        this.gameEnded = new Property<>(false, this);
        this.newGameStarted = new Property<>(false, this);
        this.start = Instant.now();
    }

    public Game(int boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this(new PositiveInteger(boardSize), blackPlayer, whitePlayer);
    }

    @NotNull
    public Player getCurrentPlayer() {
        return currentPlayer.getPropertyValue();
    }

    private void setCurrentPlayer(@NotNull final Player currentPlayer) {
        this.currentPlayer.setPropertyValueAndFireIfPropertyChange(Objects.requireNonNull(currentPlayer));
    }

    @NotNull
    public Board getBoard() {
        return board;
    }

    @NotNull
    public Stone getStoneOfPlayer(@NotNull final Player player) {
        return player.equals(blackPlayer) ? Stone.BLACK : Stone.WHITE;
    }

    public void placeNextStone(@NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {
        placeStone(currentPlayer.getPropertyValue(), coordinates);
        changeTurn();
    }

    private void placeStone(@NotNull final Player player, @NotNull final Coordinates coordinates)
            throws Board.NoMoreEmptyPositionAvailableException, Board.PositionAlreadyOccupiedException {

        board.occupyPosition(getStoneOfPlayer(Objects.requireNonNull(player)), Objects.requireNonNull(coordinates));

        setWinnerIfPlayerWon(player, coordinates);

        gameEnded.setPropertyValueAndFireIfPropertyChange(isThisGameEnded());
    }

    private void changeTurn() {
        setCurrentPlayer(currentPlayer.getPropertyValue() == blackPlayer ? whitePlayer : blackPlayer);
    }

    private void setWinnerIfPlayerWon(@NotNull Player player, @NotNull Coordinates coordinates) {
        if (hasThePlayerWonThisGame(coordinates)) {
            setWinner(player);
        }
    }

    private boolean hasThePlayerWonThisGame(@NotNull final Coordinates lastMove) {
        return board.isCoordinatesBelongingToChainOfNStones(lastMove, NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING);
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

    public synchronized boolean isThisGameEnded() {
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

    public ZonedDateTime getStart() {
        return start.atZone(ZoneId.systemDefault());
    }

    public static class GameNotEndedException extends Exception {
        public GameNotEndedException() {
            super("The game is not over.");
        }
    }
}
