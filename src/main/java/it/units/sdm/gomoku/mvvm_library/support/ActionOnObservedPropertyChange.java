package it.units.sdm.gomoku.mvvm_library.support;

import java.beans.PropertyChangeEvent;
import java.util.function.Consumer;

public class ActionOnObservedPropertyChange {

    private final String observedPropertyName;
    private final Consumer<PropertyChangeEvent> actionOnEvent;

    public ActionOnObservedPropertyChange(
            String observedPropertyName,
            Consumer<PropertyChangeEvent> actionOnEvent) {
        this.observedPropertyName = observedPropertyName;
        this.actionOnEvent = actionOnEvent;
    }

    public void performAction(PropertyChangeEvent propertyChangeEvent) {
        actionOnEvent.accept(propertyChangeEvent);
    }

    public boolean isSamePropertyName(PropertyChangeEvent otherProperty) {
        return observedPropertyName.equals(otherProperty.getPropertyName());
    }
}
