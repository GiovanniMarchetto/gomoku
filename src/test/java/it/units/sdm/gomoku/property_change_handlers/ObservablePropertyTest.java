package it.units.sdm.gomoku.property_change_handlers;

import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ObservablePropertyTest {

    private static final String sampleString = "init";
    private ConcreteFakeObservableProperty<String> observable;

    public static Stream<Arguments> allCombinationOf4RandomDistinctStrings() {
        int numberOfDistinct = 4;
        int numberOfCombinations = (int) Math.pow(2, numberOfDistinct);
        Function<String, String> padWithZeroAtBeginningToBeOfFixedLength =
                string -> String.format("%0" + numberOfDistinct + "d", Integer.valueOf(string));
        Function<String, String[]> splitOnEveryChar = string -> string.split("");
        return IntStream.range(0, numberOfCombinations)
                .mapToObj(Integer::toBinaryString)
                .map(padWithZeroAtBeginningToBeOfFixedLength)
                .map(splitOnEveryChar)
                .map(stringArray -> Arguments.of((Object[]) stringArray));
    }

    @BeforeEach
    void setup() {
        observable = new ConcreteFakeObservableProperty<>();
    }

    @Test
    void createNewInstanceWithNullValue() {
        assertNull(new ConcreteFakeObservableProperty<>().getPropertyValue());
    }

    @ParameterizedTest
    @CsvSource({"1243, sample"})
    void createCopyInstanceWithSameName(String propertyName, String propertyValue) {
        ConcreteFakeObservableProperty<String> copiedObject =
                ConcreteFakeObservableProperty.createInstanceAndGetItsCopy(propertyName, propertyValue);
        assertEquals(propertyName, copiedObject.getPropertyNameUsingReflection());
    }

    @ParameterizedTest
    @CsvSource({"1243, sample"})
    void createCopyInstanceWithSameValue(String propertyName, String propertyValue) {
        ConcreteFakeObservableProperty<String> copiedObject =
                ConcreteFakeObservableProperty.createInstanceAndGetItsCopy(propertyName, propertyValue);
        assertEquals(propertyValue, copiedObject.getPropertyValueUsingReflection());
    }

    @Test
    void getPropertyName() {
        String expectedName = String.valueOf(
                ConcreteFakeObservableProperty.getTotalNumberOfExistingDistinctInstances() - 1);
        String nameReturnedFromGetter = observable.getPropertyName();
        assertEquals(expectedName, nameReturnedFromGetter);
    }

    @Test
    void getPropertyValue() {
        observable.setPropertyValueUsingReflection(sampleString);
        assertEquals(sampleString, observable.getPropertyValue());
    }

    @ParameterizedTest
    @CsvSource({"a,b,true", "a,a,false"})
    void fireEventsWhenSetPropertyValueIfPropertyValueChangesAndShouldNotify(
            String oldValue, String newValue, boolean shouldNotify) {
        observable.setPropertyValueUsingReflection(oldValue);
        boolean x;
        AtomicReference<Boolean> observedPropertyHasNotified = new AtomicReference<>(false);
        AtomicReference<String> observedPropertyAfterSetterInvocation = new AtomicReference<>();
        new PropertyObserver<>(observable, evt -> {
            observedPropertyHasNotified.set(true);
            observedPropertyAfterSetterInvocation.set((String) evt.getNewValue());
        });
        observable.setPropertyValueAndFireIfPropertyChange(newValue);
        if (shouldNotify) { // TODO: split test (this test assert more than one thing)
            assertTrue(observedPropertyHasNotified.get());
            assertEquals(newValue, observedPropertyAfterSetterInvocation.get());
        } else {
            assertFalse(observedPropertyHasNotified.get());
        }
        assertEquals(newValue, observable.getPropertyValue());
    }

    @Test
    void equalIfSame() {
        assertEquals(observable, observable);
    }

    @SuppressWarnings({"RedundantCast"})//needed to check instanceOf in equals
    @Test
    void notEqualIfNotInstanceOf() {
        assertNotEquals((Object) observable, "");
    }

    @Test
    void notEqualIfDifferentPropertyNames() {
        Pair<ConcreteFakeObservableProperty<String>, ConcreteFakeObservableProperty<String>> pair =
                ConcreteFakeObservableProperty.createTwoInstancesWithEitherSameNameOrSameValueAndGet(true);
        assertNotEquals(pair.getKey(), pair.getValue());
    }

    @Test
    void notEqualIfDifferentPropertyValues() {
        Pair<ConcreteFakeObservableProperty<String>, ConcreteFakeObservableProperty<String>> pair =
                ConcreteFakeObservableProperty.createTwoInstancesWithEitherSameNameOrSameValueAndGet(false);
        assertNotEquals(pair.getKey(), pair.getValue());
    }

    @ParameterizedTest
    @MethodSource("allCombinationOf4RandomDistinctStrings")
    void equalIfSameNameAndValue(String name1, String name2, String value1, String value2) {
        ConcreteFakeObservableProperty<String> one = ConcreteFakeObservableProperty.createWithNameAndValueAlreadySet(name1, value1);
        ConcreteFakeObservableProperty<String> other = ConcreteFakeObservableProperty.createWithNameAndValueAlreadySet(name2, value2);
        boolean expectedEqual = name1.equals(name2) && value1.equals(value2);
        assertEquals(expectedEqual, one.equals(other));
    }

    // TODO: test hashcode + hashCode-equals contract


}