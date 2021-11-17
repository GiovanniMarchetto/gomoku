package it.units.sdm.gomoku.property_change_handlers.observable_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ObservablePropertyThatCanSetPropertyValueAndFireEvents<PropertyValueType> extends ObservableProperty<PropertyValueType> implements Cloneable {

    @NotNull
    private final ObservablePropertyThatCanSetPropertyValueAndFireEvents.PropertyValueContainer<PropertyValueType> propertyValueContainer;

    public ObservablePropertyThatCanSetPropertyValueAndFireEvents() {
        super();
        this.propertyValueContainer = new PropertyValueContainer<>(); // TODO: class needed to avoid reference to change: is this correct?
    }

    public ObservablePropertyThatCanSetPropertyValueAndFireEvents(@NotNull final ObservablePropertyThatCanSetPropertyValueAndFireEvents<PropertyValueType> observablePropertyThatCanSetPropertyValueAndFireEvents) {
        super(Objects.requireNonNull(observablePropertyThatCanSetPropertyValueAndFireEvents));
        this.propertyValueContainer = observablePropertyThatCanSetPropertyValueAndFireEvents.propertyValueContainer;
    }

    @Override
    public @Nullable PropertyValueType getPropertyValue() {
        return propertyValueContainer.getValue();
    }

    public void setPropertyValueWithoutNotifying(@Nullable final PropertyValueType propertyNewValue) {
        this.propertyValueContainer.setValue(propertyNewValue);
        super.setPropertyValue(propertyNewValue);
    }

    public void setPropertyValueAndFireIfPropertyChange(
            @Nullable final PropertyValueType propertyNewValue) {   // TODO : synchronized needed?
        PropertyValueType oldValue = getPropertyValue();
        if (!Objects.equals(oldValue, propertyNewValue)) {
            setPropertyValueWithoutNotifying(propertyNewValue);
            firePropertyChange(getPropertyName(), oldValue, getPropertyValue());
        }
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")//use copy ctor
    @Override
    public ObservablePropertyThatCanSetPropertyValueAndFireEvents<PropertyValueType> clone() {
        return new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>(this);
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