package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyObserverBooleanTest {
    ObservablePropertySettable<Boolean> observableProperty;
    AtomicBoolean valueToChange;

    @BeforeEach
    void setup() {
        observableProperty = new ObservablePropertySettable<>(false);
        valueToChange = new AtomicBoolean(false);

        new PropertyObserver<>(
                observableProperty,
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
        observableProperty.setPropertyValue(true);
        assertTrue(valueToChange.get());
    }

    @Test
    void onNoChangeButValueModifyDirectly() {
        valueToChange.set(true);
        observableProperty.setPropertyValue(false);
        assertTrue(valueToChange.get());
    }

    @Test
    void onNoChange() {
        observableProperty.setPropertyValue(false);
        assertFalse(valueToChange.get());
    }

}