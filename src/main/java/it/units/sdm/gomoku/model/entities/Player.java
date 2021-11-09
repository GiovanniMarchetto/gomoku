package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Player implements Observable {

    // TODO : test

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
        return coordinatesRequiredToContinueProperty;
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
