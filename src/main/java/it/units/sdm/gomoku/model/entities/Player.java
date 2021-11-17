package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Buffer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.model.exceptions.*;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class Player implements Observable {

    @NotNull
    private final String name;
    @NotNull
    private final Buffer<Coordinates> nextMoveBuffer;
    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> coordinatesRequiredToContinueProperty;
    @Nullable
    private Game currentGame;

    protected Player(@NotNull String playerName) {
        final int NUMBER_OF_MOVES_THAT_A_PLAYER_CAN_DO_IN_ONE_TURN = 1;
        nextMoveBuffer = new Buffer<>(NUMBER_OF_MOVES_THAT_A_PLAYER_CAN_DO_IN_ONE_TURN);
        this.name = Objects.requireNonNull(playerName);
        coordinatesRequiredToContinueProperty = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        coordinatesRequiredToContinueProperty.setPropertyValueWithoutNotifying(false);
    }

    public synchronized void setNextMove(@NotNull final Coordinates nextMoveToMake, @NotNull final Game currentGame)
            throws GameEndedException, CellOutOfBoardException, CellAlreadyOccupiedException { // TODO: test
        if (Objects.requireNonNull(currentGame)
                .isEmptyCoordinatesOnBoard(Objects.requireNonNull(nextMoveToMake))) {
            nextMoveBuffer.insert(Objects.requireNonNull(nextMoveToMake));
        } else {
            throw new CellAlreadyOccupiedException(nextMoveToMake);
        }
    }

    public void makeMove() throws NoGameSetException {             // TODO : test
        try {
            Objects.requireNonNull(currentGame).placeStoneAndChangeTurn(
                    Objects.requireNonNull(nextMoveBuffer.getAndRemoveLastElement()));
        } catch (BoardIsFullException | CellAlreadyOccupiedException | GameEndedException | CellOutOfBoardException e) {
            // TODO: handle this exception
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
        coordinatesRequiredToContinueProperty.setPropertyValueAndFireIfPropertyChange(coordinatesRequired);
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

    protected boolean isCurrentGameSet() {
        return getCurrentGame() != null;
    }
}
