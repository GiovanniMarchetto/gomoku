package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import it.units.sdm.gomoku.utils.TestUtility;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;

class ConcreteFakeObservableProperty<T> extends ObservableProperty<T> {
    private static final String propertyNameFieldName = "propertyName";
    private static final String propertyValueContainerFieldName = "propertyValueContainer";
    private static final String propertyValueFieldName = "value";
    private static final String numberOfDistinctCreatedInstancesFieldName = "numberOfDistinctCreatedInstances";

    public ConcreteFakeObservableProperty() {
        super();
    }

    public ConcreteFakeObservableProperty(ObservableProperty<T> observableProperty) {
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
    public static Pair<ConcreteFakeObservableProperty<String>, ConcreteFakeObservableProperty<String>>
    createTwoInstancesWithEitherSameNameOrSameValueAndGet(boolean sameNameAndDifferentValue) {
        String sampleString = "a";
        String differentSampleString = sampleString + "b";
        ConcreteFakeObservableProperty<String> one = ConcreteFakeObservableProperty.createWithNameAndValueAlreadySet(sampleString, sampleString);
        ConcreteFakeObservableProperty<String> other;
        if (sameNameAndDifferentValue) {
            other = ConcreteFakeObservableProperty.createWithNameAndValueAlreadySet(sampleString, differentSampleString);
        } else {
            other = ConcreteFakeObservableProperty.createWithNameAndValueAlreadySet(differentSampleString, sampleString);
        }
        return new Pair<>(one, other);
    }

    @NotNull
    public static <T> ConcreteFakeObservableProperty<T> createWithNameAndValueAlreadySet(@NotNull final String propertyName, @Nullable final T propertyValue) {
        ConcreteFakeObservableProperty<T> theInstance = new ConcreteFakeObservableProperty<>();
        theInstance.setPropertyNameUsingReflection(propertyName);
        theInstance.setPropertyValueUsingReflection(propertyValue);
        return theInstance;
    }

    @NotNull
    public static ConcreteFakeObservableProperty<String> createInstanceAndGetItsCopy(String propertyName, String propertyValue) {
        return new ConcreteFakeObservableProperty<>(
                ConcreteFakeObservableProperty.createWithNameAndValueAlreadySet(propertyName, propertyValue));
    }

    @Override
    @NotNull
    public String getPropertyName() {
        return super.getPropertyName();
    }

    @NotNull
    public String getPropertyNameUsingReflection() {
        try {
            return (String) Objects.requireNonNull(TestUtility.getFieldValue(propertyNameFieldName, this));
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
    public String toString() {
        return "ConcreteFakeObservableProperty{propertyName=" + getPropertyNameUsingReflection() +
                ", propertyValue=" + getPropertyValueUsingReflection() + "}";
    }

    @NotNull
    private Object getPropertyValueContainer() throws NoSuchFieldException, IllegalAccessException {
        return Objects.requireNonNull(TestUtility.getFieldValue(propertyValueContainerFieldName, this));
    }
}
