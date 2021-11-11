package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

public class Game implements Comparable<Game>, Observable {

    // TODO : add tests to check general rules of games

    @NotNull
    public static final PositiveInteger NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING = new PositiveInteger(5);
    @NotNull
    private final ObservableProperty<Status> gameStatus;
    @NotNull
    private final Board board;
    @NotNull
    private final Instant start;
    @NotNull
    private final Player blackPlayer, whitePlayer;
    @NotNull
    private final ObservableProperty<Player> currentPlayer;
    @Nullable
    private Player winner;  // available after the end of the game

    public Game(@NotNull PositiveInteger boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this.board = new Board(Objects.requireNonNull(boardSize));
        this.blackPlayer = Objects.requireNonNull(blackPlayer);
        this.whitePlayer = Objects.requireNonNull(whitePlayer);
        this.currentPlayer = new ObservableProperty<>();
        this.start = Instant.now();
        this.gameStatus = new ObservableProperty<>();
    }

    public Game(int boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this(new PositiveInteger(boardSize), blackPlayer, whitePlayer);
    }

    public void start() {
        gameStatus.setPropertyValueAndFireIfPropertyChange(Status.STARTED); // TODO : rename all properties "*" in "*Property"
        currentPlayer.setPropertyValueAndFireIfPropertyChange(blackPlayer);
    }

    @NotNull
    public ObservableProperty<Player> getCurrentPlayer() {
        return currentPlayer;
    }

    @NotNull
    public ObservableProperty<Status> getGameStatus() {
        return gameStatus;
    }

    @NotNull
    public Board getBoard() {
        return board;
    }

    @NotNull
    public ZonedDateTime getStart() {
        return start.atZone(ZoneId.systemDefault());
    }

    @NotNull
    public Stone.Color getColorOfPlayer(@NotNull final Player player) {
        return player.equals(blackPlayer) ? Stone.Color.BLACK : Stone.Color.WHITE;
    }

    public void placeStoneAndChangeTurn(@NotNull final Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException, GameEndedException {

        final Player player = Objects.requireNonNull(currentPlayer.getPropertyValue());

        placeStone(player, coordinates);

        setWinnerIfPlayerWon(player, coordinates);

        setGameStatusIfGameEnded();

        changeTurn();
    }

    private void placeStone(@NotNull final Player player, @NotNull final Coordinates coordinates)
            throws Board.BoardIsFullException, Board.CellAlreadyOccupiedException, GameEndedException {
        if (!isEnded()) {
            board.occupyPosition(getColorOfPlayer(Objects.requireNonNull(player)), Objects.requireNonNull(coordinates));
        } else {
            throw new GameEndedException();
        }
    }

    private void setWinnerIfPlayerWon(@NotNull Player player, @NotNull Coordinates coordinates) {
        if (hasThePlayerWonWithLastMove(coordinates)) {
            setWinner(player);
        }
    }

    private void setGameStatusIfGameEnded() {
        if (isEnded()) {
            gameStatus.setPropertyValueAndFireIfPropertyChange(Status.ENDED);
        }
    }

    private void changeTurn() {
        currentPlayer.setPropertyValueAndFireIfPropertyChange(currentPlayer.getPropertyValue() == blackPlayer ? whitePlayer : blackPlayer);
    }

    private boolean hasThePlayerWonWithLastMove(@NotNull final Coordinates lastMove) {
        return board.isCoordinatesBelongingToChainOfNStones(lastMove, NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING);
    }

    @Nullable
    public Player getWinner() throws GameNotEndedException {
        if (isEnded()) {
            return winner;
        } else {
            throw new GameNotEndedException();
        }
    }

    private void setWinner(@NotNull final Player winner) {
        this.winner = Objects.requireNonNull(winner);
    }

    public synchronized boolean isEnded() {
        return winner != null || !board.isThereAnyEmptyCell();
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

    public enum Status {STARTED, ENDED}

    public static class GameNotEndedException extends Exception {
        public GameNotEndedException() {
            super("The game is not over.");
        }
    }

    public static class GameEndedException extends Exception {
        public GameEndedException() {
            super("The game is over.");
        }
    }
}
