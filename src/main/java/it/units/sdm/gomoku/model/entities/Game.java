package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static it.units.sdm.gomoku.model.entities.Board.groupCellStreamStreamByStoneToIntStreamOfMaxNumberSameColorStones;

public class Game implements Comparable<Game>, Observable {

    @NotNull
    public static final PositiveInteger NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING = new PositiveInteger(5);
    @NotNull
    private final ObservablePropertySettable<Status> gameStatusProperty;
    @NotNull
    private final ObservablePropertySettable<Player> currentPlayerProperty;
    @NotNull
    private final Board board;
    @NotNull
    private final Instant creationTime;
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
        this.currentPlayerProperty = new ObservablePropertySettable<>();
        this.gameStatusProperty = new ObservablePropertySettable<>();
        this.creationTime = Instant.now();
    }

    public void start() {
        gameStatusProperty.setPropertyValue(Status.STARTED);
        currentPlayerProperty.setPropertyValue(blackPlayer);
    }

    @NotNull
    public ObservableProperty<Player> getCurrentPlayerProperty() {
        return new ObservablePropertyProxy<>(currentPlayerProperty);
    }

    @NotNull
    public ObservableProperty<Status> getGameStatusProperty() {
        return new ObservablePropertyProxy<>(gameStatusProperty);
    }

    @NotNull
    public Board getBoard() {
        return board;
    }

    @NotNull
    public ZonedDateTime getCreationTime() {
        return creationTime.atZone(ZoneId.systemDefault());
    }

    @NotNull
    public Color getColorOfPlayer(@NotNull final Player player) {
        return player.equals(blackPlayer) ? Color.BLACK : Color.WHITE;
    }

    public void placeStoneAndChangeTurn(@NotNull final Coordinates coordinates)
            throws BoardIsFullException, CellAlreadyOccupiedException, GameEndedException, CellOutOfBoardException {

        final Player player = Objects.requireNonNull(currentPlayerProperty.getPropertyValue());

        placeStone(player, coordinates);

        setWinnerIfPlayerWon(player, coordinates);

        setGameStatusPropertyIfGameEnded();

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

    private void setGameStatusPropertyIfGameEnded() {
        if (isEnded()) {
            gameStatusProperty.setPropertyValue(Status.ENDED);
        }
    }

    private void changeTurn() { // TODO: dovrebbe fare il controllo/throware se il gioco è finito? (secondo me si)
        currentPlayerProperty.setPropertyValue(currentPlayerProperty.getPropertyValue() == blackPlayer ? whitePlayer : blackPlayer);
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
        return this.creationTime.compareTo(other.creationTime);
    }

    @Override
    public String toString() {
        return "Game started at " + creationTime.atZone(ZoneId.systemDefault()) + "\n" +
                blackPlayer + " -> BLACK, " +
                whitePlayer + " -> WHITE" + "\n" +
                "Winner: " + winner + "\n" +
                board;
    }

    public boolean isCellAtCoordinatesEmpty(@NotNull final Coordinates proposedMove)
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

        BiFunction<Integer, Integer, Stream<Coordinates>> getAllCoordinatesInsideBoardFromVersor = (xDirection, yDirection) ->
                IntStream.rangeClosed(1, numberOfConsecutive.intValue())
                        .mapToObj(i -> new Pair<>(
                                headCoordinates.getX() + i * xDirection,
                                headCoordinates.getY() + i * yDirection))
                        .filter(pair -> pair.getKey() >= 0 && pair.getValue() >= 0)
                        .map(validPair -> new Coordinates(validPair.getKey(), validPair.getValue()))
                        .filter(board::isCoordinatesInsideBoard);


        Supplier<Stream<Stream<Coordinates>>> getAllStreamsOfCoordinatesOverAllDirections = () ->
                IntStream.rangeClosed(-1, 1)
                        .boxed()
                        .flatMap(xDirection ->
                                IntStream.rangeClosed(-1, 1).mapToObj(yDirection ->
                                        getAllCoordinatesInsideBoardFromVersor.apply(xDirection, yDirection)));

        Function<IntStream, Boolean> checkIfOneElementIsEqualToNumberOfConsecutive = intStream -> intStream
                .anyMatch(value -> value == numberOfConsecutive.intValue());

        return board
                .mapCoordStreamStreamToCellStreamStream
                .andThen(groupCellStreamStreamByStoneToIntStreamOfMaxNumberSameColorStones)
                .andThen(checkIfOneElementIsEqualToNumberOfConsecutive)
                .apply(getAllStreamsOfCoordinatesOverAllDirections.get());
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
