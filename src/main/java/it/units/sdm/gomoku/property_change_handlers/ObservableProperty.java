package it.units.sdm.gomoku.property_change_handlers;

import it.units.sdm.gomoku.mvvm_library.Observable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ObservableProperty<T> implements Observable, Cloneable {  // TODO : to be tested

    // TODO : test all events to work properly

    private static int numberOfInstances = Integer.MIN_VALUE;

    @NotNull
    private final String propertyName;

    @Nullable
    private volatile T propertyValue;   // TODO : volatile needed?

    public ObservableProperty() {
        this.propertyValue = null;
        this.propertyName = String.valueOf(numberOfInstances++);
    }

    @Nullable
    public T getPropertyValue() {
        return propertyValue;
    }

    @NotNull
    public synchronized ObservableProperty<T> setPropertyValueWithoutNotifying(@Nullable final T propertyValue) {   // TODO : synchronized needed?
        this.propertyValue = propertyValue;
        return this;
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    @NotNull
    public synchronized ObservableProperty<T> setPropertyValueAndFireIfPropertyChange(@Nullable final T propertyNewValue) {   // TODO : synchronized needed?
        T oldValue = getPropertyValue();
        if (!Objects.equals(oldValue, propertyNewValue)) {
            setPropertyValueWithoutNotifying(propertyNewValue);
            firePropertyChange(propertyName, oldValue, getPropertyValue());
        }
        return this;
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ObservableProperty<T> clone() {
        ObservableProperty<T> clone = new ObservableProperty<>();
        clone.setPropertyValueWithoutNotifying(getPropertyValue()/*TODO : .clone() but T should implement cloneable interface*/);
        return clone;
    }

    public boolean equalsValue(@NotNull final ObservableProperty<T> otherProperty) {
        return Objects.equals(propertyValue, Objects.requireNonNull(otherProperty).propertyValue);
    }

}