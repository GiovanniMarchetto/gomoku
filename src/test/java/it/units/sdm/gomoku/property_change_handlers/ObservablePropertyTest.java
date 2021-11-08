package it.units.sdm.gomoku.property_change_handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class ObservablePropertyTest {
    ObservableProperty<String> observable;
    String startString = "init";

    @BeforeEach
    void setup() {
        observable = new ObservableProperty<>();
    }

    @Test
    void getPropertyValueNotInit() {
        assertNull(observable.getPropertyValue());
    }

    @Test
    void getPropertyName() {
        int numberOfInstances = 0;
        try {
            Field propertyValueField = observable.getClass().getDeclaredField("numberOfInstances");
            propertyValueField.setAccessible(true);
            numberOfInstances = (int) propertyValueField.get(observable);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
        assertEquals(String.valueOf(numberOfInstances - 1), observable.getPropertyName());
    }

    @Test
    void getPropertyValue() {
        try {
            Field propertyValueField = observable.getClass().getDeclaredField("propertyValue");
            propertyValueField.setAccessible(true);
            propertyValueField.set(observable, startString);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail(e);
        }
        assertEquals(startString, observable.getPropertyValue());
    }

    @Test
    void setPropertyValueWithoutNotifying() {
        observable.setPropertyValueWithoutNotifying(startString);
        assertEquals(startString, observable.getPropertyValue());
    }

    @Test
    void setPropertyValueAndFireIfPropertyChange() {
        observable.setPropertyValueAndFireIfPropertyChange(startString);
        assertEquals(startString, observable.getPropertyValue());
    }

    @Test
    void testCloneAddress() {
        ObservableProperty<String> observableClone = observable.clone();
        assertNotSame(observable, observableClone);
    }

    @Test
    void testClonePropertyName() {
        ObservableProperty<String> observableClone = observable.clone();
        assertNotEquals(observable.getPropertyName(), observableClone.getPropertyName());
    }

    @Test
    void testClonePropertyValue() {
        ObservableProperty<String> observableClone = observable.clone();
        assertEquals(observable.getPropertyValue(), observableClone.getPropertyValue());
    }

    @Test
    void valueEquals() {
        ObservableProperty<String> observableClone = observable.clone();
        assertTrue(observableClone.valueEquals(observable));
    }
}