package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Game implements Comparable<Game>, Observable {

    @NotNull
    public static final PositiveInteger NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING = new PositiveInteger(5);
    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Status> gameStatus;
    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Player> currentPlayer;
    @NotNull
    private final Board board;
    @NotNull
    private final Instant start;
    @NotNull
    private final Player blackPlayer;
    @NotNull
    private final Player whitePlayer;
    @Nullable
    private Player winner;  // available after the end of the game

    public Game(@NotNull PositiveInteger boardSize, @NotNull Player blackPlayer, @NotNull Player whitePlayer) {
        this.board = new Board(Objects.requireNonNull(boardSize));
        this.blackPlayer = Objects.requireNonNull(blackPlayer);
        this.whitePlayer = Objects.requireNonNull(whitePlayer);
        this.currentPlayer = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        this.gameStatus = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        this.start = Instant.now();
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
        return new ObservablePropertyProxy<>(currentPlayer);
    }

    @NotNull
    public ObservableProperty<Status> getGameStatus() {
        return new ObservablePropertyProxy<>(gameStatus);
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
            throws BoardIsFullException, CellAlreadyOccupiedException, GameEndedException, CellOutOfBoardException {

        final Player player = Objects.requireNonNull(currentPlayer.getPropertyValue());

        placeStone(player, coordinates);

        setWinnerIfPlayerWon(player, coordinates);

        setGameStatusIfGameEnded();

        changeTurn();
    }

    private void placeStone(@NotNull final Player player, @NotNull final Coordinates coordinates)
            throws BoardIsFullException, CellAlreadyOccupiedException, GameEndedException, CellOutOfBoardException {
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

    private void changeTurn() { // TODO: dovrebbe fare il controllo/throware se il gioco è finito? (secondo me si)
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

    public boolean isEmptyCoordinatesOnBoard(@NotNull final Coordinates proposedMove)
            throws GameEndedException, CellOutOfBoardException {    // TODO : test
        if (isEnded()) {
            throw new GameEndedException();
        } else if (!board.isThereAnyEmptyCell()) {
            throw new IllegalStateException("No space on board, game should be ended but it is not.");
        }
        return board.isEmptyCellAtCoordinates(Objects.requireNonNull(proposedMove));
    }

    public boolean isHeadOfAChainOfStones(@NotNull final Coordinates headCoordinates,
                                          @NotNull final PositiveInteger numberOfConsecutive) {    // TODO: test
        return IntStream.rangeClosed(-1, 1).mapToObj(xDirection ->
                        IntStream.rangeClosed(-1, 1).mapToObj(yDirection ->
                                        IntStream.rangeClosed(1, numberOfConsecutive.intValue())
                                                .mapToObj(i -> new Pair<>(
                                                        headCoordinates.getX() + i * xDirection,
                                                        headCoordinates.getY() + i * yDirection))
                                                .filter(pair -> pair.getKey() >= 0 && pair.getValue() >= 0)
                                                .map(validPair -> new Coordinates(validPair.getKey(), validPair.getValue()))
                                                .filter(board::isCoordinatesInsideBoard)
                                                .map(board::getCellAtCoordinatesOrNullIfInvalid)
                                                .filter(Objects::nonNull)
                                                .filter(cell -> !cell.isEmpty())
                                                .collect(Collectors.groupingBy(Cell::getStone, Collectors.counting()))
                                                .values()
                                                .stream()
                                                .anyMatch(counter -> counter == numberOfConsecutive.intValue()))
                                .anyMatch(find -> find))
                .anyMatch(find -> find);
    }

    public boolean isBoardEmpty() {    // TODO: test
        return board.isEmpty();
    }

    @NotNull
    public Stream<Coordinates> getStreamOfEmptyCoordinatesOnBoard() {    // TODO: test
        return board.getStreamOfEmptyCoordinates();
    }

    public int getBoardSize() { // TODO: test
        return board.getSize();
    }

    public boolean isThereAnyEmptyCellOnBoard() {   // TODO: test
        return board.isThereAnyEmptyCell();
    }

    public enum Status {STARTED, ENDED}

}
