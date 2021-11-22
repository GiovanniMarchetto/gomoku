package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Color;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import it.units.sdm.gomoku.utils.Utility;
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
import java.util.logging.Level;
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
        this.gameStatusProperty = new ObservablePropertySettable<>(Status.NOT_STARTED);
        this.creationTime = Instant.now();
    }

    public void start() throws GameAlreadyStartedException {
        if (isNotStarted()) {
            gameStatusProperty.setPropertyValue(Status.STARTED);
            currentPlayerProperty.setPropertyValue(blackPlayer);
        } else {
            throw new GameAlreadyStartedException();
        }
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

    public int getBoardSize() {
        return board.getSize();
    }

    @NotNull
    public ZonedDateTime getCreationTime() {
        return creationTime.atZone(ZoneId.systemDefault());
    }

    @NotNull
    public Color getColorOfPlayer(@NotNull final Player player) {
        return player.equals(blackPlayer) ? Color.BLACK : Color.WHITE;
    }

    @Nullable
    public Player getWinner() throws GameNotEndedException {
        if (isEnded()) {
            return winner;
        } else {
            throw new GameNotEndedException();
        }
    }

    private void setWinner(@NotNull final Player winner) throws GameNotEndedException {
        if (isEnded()) {
            this.winner = Objects.requireNonNull(winner);
        } else {
            throw new GameNotEndedException();
        }
    }

    public boolean isBoardEmpty() {
        return board.isEmpty();
    }

    public synchronized boolean isEnded() {
        return gameStatusProperty.getPropertyValue() == Status.ENDED;
    }

    public synchronized boolean isNotStarted() {
        return gameStatusProperty.getPropertyValue() == Status.NOT_STARTED;
    }

    public void placeStoneAndChangeTurn(@NotNull final Coordinates coordinates)
            throws CellAlreadyOccupiedException, GameEndedException, CellOutOfBoardException, GameNotStartedException {

        if (isNotStarted()) {
            throw new GameNotStartedException();
        } else if (isEnded()) {
            throw new GameEndedException();
        } else {
            final Player player = Objects.requireNonNull(currentPlayerProperty.getPropertyValue());
            placeStone(player, coordinates);
            setGameStatusPropertyAndWinnerIfEndedOrElseChangeTurn(player, coordinates);
        }
    }

    private void placeStone(@NotNull final Player player, @NotNull final Coordinates coordinates)
            throws CellAlreadyOccupiedException, GameEndedException, CellOutOfBoardException, GameNotStartedException {

        if (isNotStarted()) {
            throw new GameNotStartedException();
        }
        if (isEnded()) {
            throw new GameEndedException();
        }

        try {
            board.occupyPosition(getColorOfPlayer(Objects.requireNonNull(player)), Objects.requireNonNull(coordinates));
        } catch (BoardIsFullException e) {
            Utility.getLoggerOfClass(getClass())
                    .log(Level.SEVERE, "At this point game should have already been ended", e);
            throw new IllegalStateException(e);
        }

    }

    private void setGameStatusPropertyAndWinnerIfEndedOrElseChangeTurn(
            @NotNull Player player, @NotNull Coordinates coordinates) throws GameNotStartedException {

        if (isNotStarted()) {
            throw new GameNotStartedException();
        }

        if (hasThePlayerWonWithLastMove(coordinates)) {
            gameStatusProperty.setPropertyValue(Status.ENDED);
            try {
                setWinner(player);
            } catch (GameNotEndedException e) {
                Utility.getLoggerOfClass(getClass()).log(Level.SEVERE, "Trying to set winner but game not ended", e);
                throw new IllegalStateException(e);
            }
        } else if (!board.isThereAnyEmptyCell()) {
            gameStatusProperty.setPropertyValue(Status.ENDED);
        } else {
            changeTurn();
        }
    }

    private boolean hasThePlayerWonWithLastMove(@NotNull final Coordinates lastMove) {
        return board.isCoordinatesBelongingToChainOfNStones(lastMove, NUMBER_OF_CONSECUTIVE_STONE_FOR_WINNING);
    }

    private void changeTurn() {
        currentPlayerProperty.setPropertyValue(currentPlayerProperty.getPropertyValue() == blackPlayer ? whitePlayer : blackPlayer);
    }

    @Override
    public int compareTo(@NotNull Game other) {
        return this.creationTime.compareTo(other.creationTime);
    }

    @Override
    public String toString() {
        String lineSeparator = System.lineSeparator();
        return "Game started at " + creationTime.atZone(ZoneId.systemDefault()) +
                lineSeparator + blackPlayer + " -> BLACK, " + whitePlayer + " -> WHITE" +
                lineSeparator + "Winner: " + winner +
                lineSeparator + board;
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

    @NotNull
    public Stream<Coordinates> getStreamOfEmptyCoordinatesOnBoard() {    // TODO: test
        return board.getStreamOfEmptyCoordinates();
    }

    // TODO: equals and hashcode?

    public enum Status {NOT_STARTED, STARTED, ENDED}

}
