package it.units.sdm.gomoku.property_change_handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PropertyObserverTest {
    private final ObservableProperty<String> observable = new ObservableProperty<>();
    private final String startObservableValue = "test";
    private final String changeValue = "change";
    private final String startRelatedValue = "bingo";
    private String related;

    @BeforeEach
    void setup() {
        observable.setPropertyValueWithoutNotifying(startObservableValue);
        related = startRelatedValue;

        new PropertyObserver<>(
                observable,
                propertyChangeEvent ->
                        related = (String) propertyChangeEvent.getNewValue()
        );
    }

    @Test
    void onStart() {
        assertNotEquals(startRelatedValue, startObservableValue);
        assertNotEquals(related, observable.getPropertyValue());
    }

    @Test
    void onChange() {
        observable.setPropertyValueAndFireIfPropertyChange(changeValue);
        assertEquals(observable.getPropertyValue(), related);
    }

    @Test
    void onNoChangeButValueModifyDirectly() {
        String expected = "directly";
        related = expected;
        observable.setPropertyValueAndFireIfPropertyChange(startObservableValue);
        assertEquals(expected, related);
    }

    @Test
    void onNoChange() {
        observable.setPropertyValueAndFireIfPropertyChange(startObservableValue);
        assertNotEquals(related, observable.getPropertyValue());
    }

    @Test
    void onChangeWithoutNotify() {
        observable.setPropertyValueWithoutNotifying(changeValue);
        assertNotEquals(related,observable.getPropertyValue());
    }

}