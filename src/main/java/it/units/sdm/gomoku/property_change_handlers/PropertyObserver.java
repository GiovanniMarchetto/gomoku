package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.function.Consumer;

public class PropertyObserver<ObservedPropertyValueType> implements Observer {
    // TODO : to be tested

    @NotNull
    private final Consumer<PropertyChangeEvent> actionOnPropertyChange;
    @Nullable
    private volatile PropertyChangeEvent lastObservedEvt;   // TODO : only for tests purposes

    public PropertyObserver(@NotNull final ObservableProperty<ObservedPropertyValueType> observedProperty,
                            @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {
        this.actionOnPropertyChange = Objects.requireNonNull(actionOnPropertyChange);
        observe(Objects.requireNonNull(observedProperty));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        lastObservedEvt = evt;
        actionOnPropertyChange.accept(evt);
    }
}
