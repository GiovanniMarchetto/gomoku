package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ObservableProperty<PropertyValueType> implements Observable, Cloneable {  // TODO : to be tested

    // TODO : test all events to work properly

    private static int numberOfInstances = Integer.MIN_VALUE;

    @NotNull
    private String propertyName;

    @Nullable
    private volatile PropertyValueType propertyValue;   // TODO : volatile needed? Atomic reference better?

    public ObservableProperty() {
        this.propertyValue = null;
        this.propertyName = String.valueOf(numberOfInstances++);
    }

    @Nullable
    public PropertyValueType getPropertyValue() {
        return propertyValue;
    }

    @NotNull
    public synchronized ObservableProperty<PropertyValueType> setPropertyValueWithoutNotifying(@Nullable final PropertyValueType propertyValue) {   // TODO : synchronized needed?
        this.propertyValue = propertyValue;
        return this;
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    @NotNull
    public synchronized ObservableProperty<PropertyValueType> setPropertyValueAndFireIfPropertyChange(
            @Nullable final PropertyValueType propertyNewValue) {   // TODO : synchronized needed?
        PropertyValueType oldValue = getPropertyValue();
        if (!Objects.equals(oldValue, propertyNewValue)) {
            setPropertyValueWithoutNotifying(propertyNewValue);
            firePropertyChange(propertyName, oldValue, getPropertyValue());
        }
        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ObservableProperty<PropertyValueType> clone() {
        ObservableProperty<PropertyValueType> clone = new ObservableProperty<>();
        clone.propertyName = this.propertyName;
        clone.setPropertyValueWithoutNotifying(getPropertyValue()/*TODO : .clone() but PropertyValueType should implement cloneable interface (use copy-ctor)*/);
        return clone;
    }

    @Override
    public boolean equals(Object o) {   //  todo: test
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservableProperty<?> that = (ObservableProperty<?>) o;
        return Objects.equals(propertyName, that.propertyName)
                && Objects.equals(propertyValue, that.propertyValue);
    }

    @Override
    public int hashCode() {   // TODO : test
        return propertyName.hashCode();
    }

    public boolean valueEquals(@NotNull final ObservableProperty<PropertyValueType> otherProperty) {
        return Objects.equals(propertyValue, Objects.requireNonNull(otherProperty).propertyValue);
    }

}