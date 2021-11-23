package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("ClassCanBeRecord")
public class PropertyObserver<ObservedPropertyValueType> implements Observer {

    @NotNull
    private final ObservableProperty<ObservedPropertyValueType> observedProperty;

    @NotNull
    private final Consumer<PropertyChangeEvent> actionOnPropertyChange;

    public PropertyObserver(@NotNull final ObservableProperty<ObservedPropertyValueType> observedProperty,
                            @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {
        this.actionOnPropertyChange = Objects.requireNonNull(actionOnPropertyChange);
        this.observedProperty = Objects.requireNonNull(observedProperty);
        observe(observedProperty);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        actionOnPropertyChange.accept(evt);
    }

    public void stopObserving() {
        stopObserving(observedProperty);
    }
}
