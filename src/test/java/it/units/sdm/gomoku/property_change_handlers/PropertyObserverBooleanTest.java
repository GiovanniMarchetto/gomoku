package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyObserverBooleanTest {
    ObservablePropertyThatCanSetPropertyValueAndFireEvents<Boolean> observable;
    AtomicBoolean valueToChange;

    @BeforeEach
    void setup() {
        observable = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>(false);
        valueToChange = new AtomicBoolean(false);

        new PropertyObserver<>(
                observable,
                propertyChangeEvent ->
                        valueToChange.set((Boolean) propertyChangeEvent.getNewValue())
        );
    }

    @Test
    void onStart() {
        assertFalse(valueToChange.get());
    }

    @Test
    void onChange() {
        observable.setPropertyValue(true);
        assertTrue(valueToChange.get());
    }

    @Test
    void onNoChangeButValueModifyDirectly() {
        valueToChange.set(true);
        observable.setPropertyValue(false);
        assertTrue(valueToChange.get());
    }

    @Test
    void onNoChange() {
        observable.setPropertyValue(false);
        assertFalse(valueToChange.get());
    }

}