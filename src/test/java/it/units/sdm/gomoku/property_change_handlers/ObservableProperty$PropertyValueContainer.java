package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ObservableProperty$PropertyValueContainer {

    @NotNull
    private static final String SAMPLE_VALUE = "default value";
    @NotNull
    private static final String DIFFERENT_SAMPLE_VALUE = SAMPLE_VALUE + "DIFFERENT FROM PREVIOUS VALUE";
    @NotNull
    private static final ObservableProperty<String> observableProperty = new ConcreteFakeObservableProperty<>(SAMPLE_VALUE);
    @NotNull
    private static Object valueContainerOfObservableProperty;

    @NotNull
    private static Object getPropertyValueContainerOf(ObservableProperty<?> observableProperty) throws NoSuchFieldException, IllegalAccessException {
        return Objects.requireNonNull(
                TestUtility.getFieldValue("propertyValueContainer", observableProperty));
    }

    private static Pair<?, ?> getPairOfEqualsButNotSameContainers() throws NoSuchFieldException, IllegalAccessException {
        Object container1 = getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(DIFFERENT_SAMPLE_VALUE));
        Object container2 = getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(DIFFERENT_SAMPLE_VALUE));
        assert container1 != container2;
        return new Pair<>(container1, container2);
    }

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        valueContainerOfObservableProperty = getPropertyValueContainerOf(observableProperty);
    }

    @Test
    void testValueGetter() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Object objectFromGetter = invokeValueGetterViaReflection(getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(SAMPLE_VALUE)));
        assertEquals(SAMPLE_VALUE, objectFromGetter);
    }

    private Object invokeValueGetterViaReflection(Object container) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        return TestUtility.invokeMethodOnObject(container, "getValue");
    }

    @Test
    void testValueSetter() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Method valueSetter = valueContainerOfObservableProperty.getClass().getDeclaredMethod("setValue", Object.class);
        valueSetter.setAccessible(true);
        valueSetter.invoke(valueContainerOfObservableProperty, DIFFERENT_SAMPLE_VALUE);
        assertEquals(DIFFERENT_SAMPLE_VALUE, invokeValueGetterViaReflection(valueContainerOfObservableProperty));
    }

    @Test
    void equalsIfSame() {
        assertEquals(valueContainerOfObservableProperty, valueContainerOfObservableProperty);
    }

    @Test
    void notEqualsIfDifferentClass() {
        assertNotEquals(valueContainerOfObservableProperty, "a string");
    }

    @Test
    void notEqualsIfDifferentValue() throws NoSuchFieldException, IllegalAccessException {
        assertNotEquals(
                getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(SAMPLE_VALUE)),
                getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(DIFFERENT_SAMPLE_VALUE)));
    }

    @Test
    void equalsIfEqualValue() throws NoSuchFieldException, IllegalAccessException {
        Pair<?, ?> pairOfContainers = getPairOfEqualsButNotSameContainers();
        Object container1 = pairOfContainers.getKey();
        Object container2 = pairOfContainers.getValue();
        assertEquals(container1, container2);
    }

    @Test
    void sameHashCodeIfEquals() throws NoSuchFieldException, IllegalAccessException {
        Pair<?, ?> pairOfContainers = getPairOfEqualsButNotSameContainers();
        Object container1 = pairOfContainers.getKey();
        Object container2 = pairOfContainers.getValue();
        assertEquals(container1.hashCode(), container2.hashCode());
    }

    @Test
    void sameHashCodeIfSame() {
        assertEquals(valueContainerOfObservableProperty, valueContainerOfObservableProperty);
    }

    @Test
    void notEqualsIfDifferentHashCode() throws NoSuchFieldException, IllegalAccessException {
        Object valueContainer1 = getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(SAMPLE_VALUE));
        Object valueContainer2 = getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(DIFFERENT_SAMPLE_VALUE));
        int hashCode1 = valueContainer1.hashCode();
        int hashCode2 = valueContainer2.hashCode();
        assert hashCode1 != hashCode2;
        assertNotEquals(hashCode1, hashCode2);
    }

    @Test
    void testToString() throws NoSuchFieldException, IllegalAccessException {
        assertEquals(
                SAMPLE_VALUE,
                getPropertyValueContainerOf(new ConcreteFakeObservableProperty<>(SAMPLE_VALUE)).toString());
    }

}
