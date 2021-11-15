package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.model.custom_types.Buffer;
import it.units.sdm.gomoku.model.custom_types.Coordinates;
import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyProxy;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Player implements Observable {

    @NotNull
    private final String name;

    @NotNull
    private final Buffer<Coordinates> nextMoveBuffer;

    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> coordinatesRequiredToContinueProperty;

    protected Player(@NotNull String name) {
        final int NUMBER_OF_MOVES_THAT_A_PLAYER_CAN_DO_IN_ONE_TURN = 1;
        nextMoveBuffer = new Buffer<>(NUMBER_OF_MOVES_THAT_A_PLAYER_CAN_DO_IN_ONE_TURN);
        this.name = Objects.requireNonNull(name);
        coordinatesRequiredToContinueProperty = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
        coordinatesRequiredToContinueProperty.setPropertyValueWithoutNotifying(false);
    }

    public synchronized void setNextMove(@NotNull final Coordinates nextMoveToMake, @NotNull final Game currentGame)
            throws Game.GameEndedException, Board.CellOutOfBoardException, Board.CellAlreadyOccupiedException { // TODO: test
        if (Objects.requireNonNull(currentGame)
                .isEmptyCoordinatesOnBoard(Objects.requireNonNull(nextMoveToMake))) {
            nextMoveBuffer.insert(Objects.requireNonNull(nextMoveToMake));
        } else {
            throw new Board.CellAlreadyOccupiedException(nextMoveToMake);
        }
    }

    public void makeMove(@NotNull final Game currentGame) {             // TODO : test
        try {
            Objects.requireNonNull(currentGame).placeStoneAndChangeTurn(
                    Objects.requireNonNull(nextMoveBuffer.getAndRemoveLastElement()));
        } catch (Board.BoardIsFullException | Board.CellAlreadyOccupiedException | Game.GameEndedException | Board.CellOutOfBoardException e) {
            // TODO: handle this exception
        }
    }

    @NotNull
    public String getName() {
        return name;
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
}
