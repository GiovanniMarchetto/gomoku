package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservablePropertyThatCanSetPropertyValueAndFireEvents;
import it.units.sdm.gomoku.utils.TestUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class ObservablePropertyTest {
    ObservablePropertyThatCanSetPropertyValueAndFireEvents<String> observable;
    String startString = "init";

    @BeforeEach
    void setup() {
        observable = new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>();
    }

    @Test
    void getPropertyValueNotInit() {
        assertNull(observable.getPropertyValue());
    }

    @Test
    void getPropertyName() {
        int numberOfInstances = 0;
        try {
            Field propertyValueField = TestUtility.getFieldAlreadyMadeAccessible(observable.getClass(), "numberOfDistinctCreatedInstances");
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
            TestUtility.setFieldValue("value", startString,
                    Objects.requireNonNull(TestUtility.getFieldValue("propertyValueContainer", observable)));
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
        ObservablePropertyThatCanSetPropertyValueAndFireEvents<String> observableClone = observable.clone();
        assertEquals(observable.getPropertyName(), observableClone.getPropertyName());
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