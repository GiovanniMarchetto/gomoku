package it.units.sdm.gomoku.property_change_handlers.observable_properties;

import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public abstract class ObservableProperty<PropertyValueType> implements Observable {


    private static int numberOfDistinctCreatedInstances = Integer.MIN_VALUE;

    @NotNull
    private final String propertyName;
    @NotNull
    private final ObservableProperty.PropertyValueContainer<PropertyValueType> propertyValueContainer;

    protected ObservableProperty() {
        this.propertyValueContainer = new PropertyValueContainer<>();
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
    protected synchronized ObservableProperty<PropertyValueType> setPropertyValue(
            @Nullable final PropertyValueType newPropertyValue) {
        PropertyValueType oldValue = getPropertyValue();
        if (!Objects.equals(oldValue, newPropertyValue)) {
            this.propertyValueContainer.setValue(newPropertyValue);
            firePropertyChange(propertyName, oldValue, getPropertyValue());
        }
        return this;
    }

    @NotNull
    protected String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ObservableProperty<?> that)) return false;
        return Objects.equals(propertyName, that.propertyName)
                && Objects.equals(propertyValueContainer, that.propertyValueContainer);
    }

    public boolean valueEquals(@NotNull final ObservableProperty<PropertyValueType> otherProperty) {
        return Objects.equals(propertyValueContainer, Objects.requireNonNull(otherProperty).propertyValueContainer);
    }

    @Override
    public int hashCode() {
        return propertyName.hashCode();
    }

    private static class PropertyValueContainer<ValueType> {
        @Nullable
        private ValueType value;

        @Nullable
        public synchronized ValueType getValue() {
            return value;
        }

        public synchronized void setValue(@Nullable ValueType value) {
            this.value = value;
        }

        @Override
        public synchronized boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PropertyValueContainer<?> that = (PropertyValueContainer<?>) o;
            return Objects.equals(value, that.value);
        }

        @Override
        public synchronized int hashCode() {
            return value != null ? value.hashCode() : 0;
        }

        @Override
        public synchronized String toString() {
            return String.valueOf(value);
        }
    }

}