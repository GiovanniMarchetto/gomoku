package it.units.sdm.gomoku.property_change_handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PropertyObserverBooleanTest {
    ObservableProperty<Boolean> observable;
    AtomicBoolean valueToChange;

    @BeforeEach
    void setup() {
        observable = new ObservableProperty<>();
        observable.setPropertyValueWithoutNotifying(false);
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
        observable.setPropertyValueAndFireIfPropertyChange(true);
        assertTrue(valueToChange.get());
    }

    @Test
    void onNoChangeButValueModifyDirectly() {
        valueToChange.set(true);
        observable.setPropertyValueAndFireIfPropertyChange(false);
        assertTrue(valueToChange.get());
    }

    @Test
    void onNoChange() {
        observable.setPropertyValueAndFireIfPropertyChange(false);
        assertFalse(valueToChange.get());
    }

    @Test
    void onChangeWithoutNotify() {
        observable.setPropertyValueWithoutNotifying(true);
        assertFalse(valueToChange.get());
    }

}