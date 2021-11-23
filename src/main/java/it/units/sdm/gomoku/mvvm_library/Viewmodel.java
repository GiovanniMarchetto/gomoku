package it.units.sdm.gomoku.mvvm_library;

import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class Viewmodel {

    @NotNull
    private final List<PropertyObserver<?>> modelPropertyObservers;

    public Viewmodel() {
        this.modelPropertyObservers = new ArrayList<>();
    }

    protected <S> void addObservedProperty(
            @NotNull final ObservableProperty<S> observableProperty,
            @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {

        modelPropertyObservers.add(new PropertyObserver<>(
                Objects.requireNonNull(observableProperty), Objects.requireNonNull(actionOnPropertyChange)));
    }

    protected void stopObservingAllProperties() {
        modelPropertyObservers.forEach(PropertyObserver::stopObserving);
        modelPropertyObservers.clear();
    }
}