package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Buffer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.custom_types.PositiveInteger;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.Stream;

public abstract class Player implements Observable {

    private static final int NUMBER_OF_MOVES_THAT_A_PLAYER_CAN_DO_IN_ONE_TURN = 1;
    @NotNull
    private final String name;
    @NotNull
    private final Buffer<Coordinates> nextMoveBuffer;
    @NotNull
    private final ObservablePropertySettable<Boolean> coordinatesRequiredToContinueProperty;
    @Nullable
    private Game currentGame;

    protected Player(@NotNull final String playerName) {
        this.nextMoveBuffer = new Buffer<>(NUMBER_OF_MOVES_THAT_A_PLAYER_CAN_DO_IN_ONE_TURN);
        this.name = Objects.requireNonNull(playerName);
        this.coordinatesRequiredToContinueProperty = new ObservablePropertySettable<>(false);
    }

    public void makeMove() throws BoardIsFullException,
            GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        if (currentGame == null) {
            throw new IllegalStateException(new NullGameException());
        }
        Coordinates nextMove = Objects.requireNonNull(nextMoveBuffer.getAndRemoveLastElement());
        currentGame.placeStoneAndChangeTurn(nextMove);
    }

    public synchronized void setMoveToBeMade(@NotNull final Coordinates nextMoveToMake)
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException {
        if (currentGame == null) {
            throw new IllegalStateException(new NullGameException());
        }
        Objects.requireNonNull(nextMoveToMake);

        if (currentGame.isCellAtCoordinatesEmpty(nextMoveToMake)) {
            nextMoveBuffer.insert(nextMoveToMake);
        } else {
            throw new CellAlreadyOccupiedException(nextMoveToMake);
        }
    }

    @NotNull
    public String getName() {
        return name;
    }

    @Nullable
    protected Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(@NotNull Game currentGame) {
        this.currentGame = currentGame;
    }

    @Override
    public String toString() {
        return getName();
    }

    @NotNull
    public ObservableProperty<Boolean> getCoordinatesRequiredToContinueProperty() {
        return new ObservablePropertyProxy<>(coordinatesRequiredToContinueProperty);
    }

    protected void setCoordinatesRequired(final boolean coordinatesRequired) {
        coordinatesRequiredToContinueProperty.setPropertyValue(coordinatesRequired);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return name.equals(((Player) o).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    protected boolean isFirstMove() {   // TODO: test
        return Objects.requireNonNull(currentGame).isBoardEmpty();
    }

    protected Stream<Coordinates> getStreamOfEmptyCoordinatesOnBoardInCurrentGame() {
        return Objects.requireNonNull(currentGame).getStreamOfEmptyCoordinatesOnBoard();
    }

    protected boolean isHeadOfAChainOfStonesInCurrentGame(@NotNull final Coordinates coord, @NotNull final PositiveInteger positiveInteger) {
        return Objects.requireNonNull(currentGame)
                .isHeadOfAChainOfStones(Objects.requireNonNull(coord), Objects.requireNonNull(positiveInteger));
    }

    protected int getBoardSizeInCurrentGame() {
        return Objects.requireNonNull(currentGame).getBoardSize();
    }

    protected boolean isCurrentGameSet() {    // TODO: test
        return getCurrentGame() != null;
    }
}
