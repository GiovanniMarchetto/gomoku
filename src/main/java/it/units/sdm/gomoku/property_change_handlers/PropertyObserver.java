package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observer;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.function.Consumer;

public class PropertyObserver<T> implements Observer {
    // TODO : to be tested

    private final Consumer<PropertyChangeEvent> actionOnPropertyChange;

    public PropertyObserver(@NotNull final ObservableProperty<T> observedProperty,
                            @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {
        this.actionOnPropertyChange = Objects.requireNonNull(actionOnPropertyChange);
        Objects.requireNonNull(observedProperty).beObservedBy(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        actionOnPropertyChange.accept(evt);
    }
}
