package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertySettable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PropertyObserverTest {
    private final String startObservableValue = "test";
    private final String changeValue = "change";
    private final String startRelatedValue = "bingo";
    private String related;
    private final ObservablePropertySettable<String> observableProperty = new ObservablePropertySettable<>(startObservableValue);

    @BeforeEach
    void setup() {
        related = startRelatedValue;

        new PropertyObserver<>(
                observableProperty,
                propertyChangeEvent ->
                        related = (String) propertyChangeEvent.getNewValue()
        );
    }

    @Test
    void onStart() {
        assertNotEquals(startRelatedValue, startObservableValue);
        assertNotEquals(related, observableProperty.getPropertyValue());
    }

    @Test
    void onChange() {
        observableProperty.setPropertyValue(changeValue);
        assertEquals(observableProperty.getPropertyValue(), related);
    }

    @Test
    void onNoChangeButValueModifyDirectly() {
        String expected = "directly";
        related = expected;
        observableProperty.setPropertyValue(startObservableValue);
        assertEquals(expected, related);
    }

    @Test
    void onNoChange() {
        observableProperty.setPropertyValue(startObservableValue);
        assertNotEquals(related, observableProperty.getPropertyValue());
    }

}