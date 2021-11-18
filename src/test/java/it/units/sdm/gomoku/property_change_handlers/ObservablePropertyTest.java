package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ObservablePropertyTest {

    private static final String sampleString = "init";
    private FakeObservableProperty<String> observable;

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
        observable = new FakeObservableProperty<>();
    }

    @Test
    void createNewInstanceWithNullValue() {
        assertNull(new FakeObservableProperty<>().getPropertyValue());
    }

    @ParameterizedTest
    @CsvSource({"1243, sample"})
    void createCopyInstanceWithSameName(String propertyName, String propertyValue) {
        FakeObservableProperty<String> copiedObject =
                FakeObservableProperty.createInstanceAndGetItsCopy(propertyName, propertyValue);
        assertEquals(propertyName, copiedObject.getPropertyNameUsingReflection());
    }

    @ParameterizedTest
    @CsvSource({"1243, sample"})
    void createCopyInstanceWithSameValue(String propertyName, String propertyValue) {
        FakeObservableProperty<String> copiedObject =
                FakeObservableProperty.createInstanceAndGetItsCopy(propertyName, propertyValue);
        assertEquals(propertyValue, copiedObject.getPropertyValueUsingReflection());
    }

    @Test
    void getPropertyName() {
        String expectedName = String.valueOf(
                FakeObservableProperty.getTotalNumberOfExistingDistinctInstances() - 1);
        String nameReturnedFromGetter = observable.getPropertyName();
        assertEquals(expectedName, nameReturnedFromGetter);
    }

    @Test
    void getPropertyValue() {
        observable.setPropertyValueUsingReflection(sampleString);
        assertEquals(sampleString, observable.getPropertyValue());
    }

    @Test
    void setPropertyValueWithoutNotifying() {
        observable.setPropertyValueWithoutNotifying(sampleString);
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
        Pair<FakeObservableProperty<String>, FakeObservableProperty<String>> pair =
                FakeObservableProperty.createTwoInstancesWithEitherSameNameOrSameValueAndGet(true);
        assertNotEquals(pair.getKey(), pair.getValue());
    }

    @Test
    void notEqualIfDifferentPropertyValues() {
        Pair<FakeObservableProperty<String>, FakeObservableProperty<String>> pair =
                FakeObservableProperty.createTwoInstancesWithEitherSameNameOrSameValueAndGet(false);
        assertNotEquals(pair.getKey(), pair.getValue());
    }

    @ParameterizedTest
    @MethodSource("allCombinationOf4RandomDistinctStrings")
    void equalIfSameNameAndValue(String name1, String name2, String value1, String value2) {
        FakeObservableProperty<String> one = FakeObservableProperty.createWithNameAndValueAlreadySet(name1, value1);
        FakeObservableProperty<String> other = FakeObservableProperty.createWithNameAndValueAlreadySet(name2, value2);
        boolean expectedEqual = name1.equals(name2) && value1.equals(value2);
        assertEquals(expectedEqual, one.equals(other));
    }

    // TODO: test hashcode + hashCode-equals contract


    private static class FakeObservableProperty<T> extends ObservableProperty<T> {
        private static final String propertyNameFieldName = "propertyName";
        private static final String propertyValueContainerFieldName = "propertyValueContainer";
        private static final String propertyValueFieldName = "value";
        private static final String numberOfDistinctCreatedInstancesFieldName = "numberOfDistinctCreatedInstances";

        public FakeObservableProperty() {
            super();
        }

        public FakeObservableProperty(ObservableProperty<T> observableProperty) {
            super(observableProperty);
        }

        public static int getTotalNumberOfExistingDistinctInstances() {
            try {
                return (int) TestUtility
                        .getFieldAlreadyMadeAccessible(ObservableProperty.class, numberOfDistinctCreatedInstancesFieldName)
                        .get(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
                return -1;
            }
        }

        @NotNull
        public static Pair<FakeObservableProperty<String>, FakeObservableProperty<String>>
        createTwoInstancesWithEitherSameNameOrSameValueAndGet(boolean sameNameAndDifferentValue) {
            String differentSampleString = sampleString + "a";
            FakeObservableProperty<String> one = FakeObservableProperty.createWithNameAndValueAlreadySet(sampleString, sampleString);
            FakeObservableProperty<String> other;
            if (sameNameAndDifferentValue) {
                other = FakeObservableProperty.createWithNameAndValueAlreadySet(sampleString, differentSampleString);
            } else {
                other = FakeObservableProperty.createWithNameAndValueAlreadySet(differentSampleString, sampleString);
            }
            return new Pair<>(one, other);
        }

        @NotNull
        public static <T> FakeObservableProperty<T> createWithNameAndValueAlreadySet(@NotNull final String propertyName, @Nullable final T propertyValue) {
            FakeObservableProperty<T> theInstance = new FakeObservableProperty<>();
            theInstance.setPropertyNameUsingReflection(propertyName);
            theInstance.setPropertyValueUsingReflection(propertyValue);
            return theInstance;
        }

        @NotNull
        public static FakeObservableProperty<String> createInstanceAndGetItsCopy(String propertyName, String propertyValue) {
            return new FakeObservableProperty<>(
                    FakeObservableProperty.createWithNameAndValueAlreadySet(propertyName, propertyValue));
        }

        @Override
        @NotNull
        public String getPropertyName() {
            return super.getPropertyName();
        }

        @NotNull
        public String getPropertyNameUsingReflection() {
            try {
                return (String) TestUtility.getFieldValue(propertyNameFieldName, this);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
                return null;
            }
        }

        public void setPropertyNameUsingReflection(@NotNull final String propertyName) {
            try {
                TestUtility.setFieldValue(propertyNameFieldName, Objects.requireNonNull(propertyName), this);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
            }
        }

        @Nullable
        public Object getPropertyValueUsingReflection() {
            try {
                return TestUtility.getFieldValue(propertyValueFieldName, getPropertyValueContainer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
                return null;
            }
        }

        public void setPropertyValueUsingReflection(@Nullable final T propertyValue) {
            try {
                TestUtility.setFieldValue(propertyValueFieldName, propertyValue, getPropertyValueContainer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                fail(e);
            }
        }

        @Override
        @NotNull
        protected ObservableProperty<T> setPropertyValueAndFireIfPropertyChange(@Nullable T newPropertyValue) {
            return super.setPropertyValueAndFireIfPropertyChange(newPropertyValue);
        }

        @Override
        @NotNull
        protected synchronized ObservableProperty<T> setPropertyValueWithoutNotifying(@Nullable T newPropertyValue) {
            return super.setPropertyValueWithoutNotifying(newPropertyValue);
        }

        @Override
        public String toString() {
            return "FakeObservableProperty{propertyName=" + getPropertyNameUsingReflection() +
                    ", propertyValue=" + getPropertyValueUsingReflection() + "}";
        }

        @NotNull
        private Object getPropertyValueContainer() throws NoSuchFieldException, IllegalAccessException {
            return Objects.requireNonNull(TestUtility.getFieldValue(propertyValueContainerFieldName, this));
        }
    }
}