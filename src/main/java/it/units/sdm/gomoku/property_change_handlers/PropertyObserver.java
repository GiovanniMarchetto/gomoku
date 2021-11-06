package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observer;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.function.Consumer;

public class PropertyObserver<T> implements Observer {

    private final Consumer<PropertyChangeEvent> actionOnPropertyChange;
    private final ObservableProperty<T> observedProperty;

    public PropertyObserver(@NotNull final ObservableProperty<T> observedProperty,
                            @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {
        this.actionOnPropertyChange = Objects.requireNonNull(actionOnPropertyChange);
        this.observedProperty = Objects.requireNonNull(observedProperty);
        this.observedProperty.addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        actionOnPropertyChange.accept(evt);
    }
}
