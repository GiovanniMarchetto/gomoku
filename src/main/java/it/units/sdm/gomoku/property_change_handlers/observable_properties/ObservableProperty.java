package it.units.sdm.gomoku.property_change_handlers.observable_properties;

import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ObservableProperty<PropertyValueType> implements Observable {  // TODO : to be tested

    // TODO : test all events to work properly

    private static int numberOfDistinctCreatedInstances = Integer.MIN_VALUE;

    @NotNull
    private final String propertyName;
    @Nullable
    private PropertyValueType propertyValue;

    protected ObservableProperty() {
        this.propertyName = String.valueOf(numberOfDistinctCreatedInstances++);
        this.propertyValue = null;
    }

    protected ObservableProperty(@NotNull final ObservableProperty<PropertyValueType> observableProperty) {
        this.propertyName = observableProperty.propertyName;
        this.propertyValue = observableProperty.propertyValue;
    }

    @Nullable
    public PropertyValueType getPropertyValue() {
        return propertyValue;
    }

    protected void setPropertyValue(@Nullable final PropertyValueType propertyValue) {
        this.propertyValue = propertyValue;
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {   //  todo: test
        if (this == o)
            return true; // todo: replace all reference comparison with a method "isSameReference" instead of == ? It may be an interface like "ReferenceComparable" with only one mehod "compareRef(Object other)" with a default implementation: in this way classes should simply declare to implement the interface without implementing any method
        if (!(o instanceof ObservableProperty<?> that)) return false;
        return Objects.equals(propertyName, that.propertyName)
                && Objects.equals(propertyValue, that.propertyValue);
    }

    public boolean valueEquals(@NotNull final ObservableProperty<PropertyValueType> otherProperty) {
        return Objects.equals(propertyValue, Objects.requireNonNull(otherProperty).propertyValue);
    }

    @Override
    public int hashCode() {   // TODO : test
        return propertyName.hashCode();
    }

}