package it.units.sdm.gomoku.model.entities;

import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.property_change_handlers.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.ProxyObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.SettableObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class Player implements Observable {

    @NotNull
    private final String name;

    @NotNull
    private final SettableObservableProperty<Boolean> coordinatesRequiredToContinueProperty;

    protected Player(@NotNull String name) {
        this.name = Objects.requireNonNull(name);
        coordinatesRequiredToContinueProperty = new SettableObservableProperty<>();
        coordinatesRequiredToContinueProperty.setPropertyValueWithoutNotifying(false);
    }

    public abstract void makeMove(@NotNull final Game currentGame);

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
        return new ProxyObservableProperty<>(coordinatesRequiredToContinueProperty);
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
