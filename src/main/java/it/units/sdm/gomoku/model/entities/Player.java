package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Player implements Observable {

    @NotNull
    private final String name;

    @NotNull
    private final ObservableProperty<Boolean> coordinatesRequiredToContinueProperty;

    protected Player(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
        coordinatesRequiredToContinueProperty = new ObservableProperty<>();
        coordinatesRequiredToContinueProperty.setPropertyValueAndFireIfPropertyChange(false);
    }

    public abstract void makeMove(@NotNull final Game currentGame) throws Board.BoardIsFullException;

    @Override
    public String toString() {
        return name;
    }

    @NotNull
    public ObservableProperty<Boolean> getCoordinatesRequiredToContinueProperty() {
        return coordinatesRequiredToContinueProperty;
    }

    protected void setCoordinatesRequired(final boolean coordinatesRequired) {
        coordinatesRequiredToContinueProperty.setPropertyValueAndFireIfPropertyChange(coordinatesRequired);
    }
}
