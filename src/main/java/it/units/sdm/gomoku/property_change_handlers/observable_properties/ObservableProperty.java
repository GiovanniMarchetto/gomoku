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
    @NotNull
    private final ObservableProperty.PropertyValueContainer<PropertyValueType> propertyValueContainer;

    protected ObservableProperty() {
        this.propertyValueContainer = new PropertyValueContainer<>(); // TODO: class needed to avoid reference to change: is this correct?
        this.propertyName = String.valueOf(numberOfDistinctCreatedInstances++);
    }

    protected ObservableProperty(@Nullable final PropertyValueType initialValue) {
        this();
        this.propertyValueContainer.setValue(initialValue);
    }

    protected ObservableProperty(@NotNull final ObservableProperty<PropertyValueType> observableProperty) {
        this.propertyName = observableProperty.propertyName;
        this.propertyValueContainer = observableProperty.propertyValueContainer;
    }

    @Nullable
    public PropertyValueType getPropertyValue() {
        return propertyValueContainer.getValue();
    }

    @NotNull
    protected String getPropertyName() {
        return propertyName;
    }

    @NotNull
    protected synchronized ObservableProperty<PropertyValueType> setPropertyValue(
            @Nullable final PropertyValueType newPropertyValue) {   // TODO : synchronized needed?
        PropertyValueType oldValue = getPropertyValue();
        if (!Objects.equals(oldValue, newPropertyValue)) {
            this.propertyValueContainer.setValue(newPropertyValue);
            firePropertyChange(propertyName, oldValue, getPropertyValue());
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {   //  todo: test
        if (this == o)
            return true; // todo: replace all reference comparison with a method "isSameReference" instead of == ? It may be an interface like "ReferenceComparable" with only one mehod "compareRef(Object other)" with a default implementation: in this way classes should simply declare to implement the interface without implementing any method
        if (!(o instanceof ObservableProperty<?> that)) return false;
        return Objects.equals(propertyName, that.propertyName)
                && Objects.equals(propertyValueContainer, that.propertyValueContainer);
    }

    public boolean valueEquals(@NotNull final ObservableProperty<PropertyValueType> otherProperty) {
        return Objects.equals(propertyValueContainer, Objects.requireNonNull(otherProperty).propertyValueContainer);
    }

    @Override
    public int hashCode() {   // TODO : test
        return propertyName.hashCode();
    }

    private static class PropertyValueContainer<ValueType> {    // TODO : to be tested
        @Nullable
        private volatile ValueType value;   // TODO : volatile needed? Atomic reference better?

        @Nullable
        public ValueType getValue() {
            return value;
        }

        public void setValue(@Nullable ValueType value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PropertyValueContainer<?> that = (PropertyValueContainer<?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public int hashCode() {
            //noinspection ConstantConditions   // just checked to be non-null
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }

}