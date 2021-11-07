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
    private T propertyValue;

    public ObservableProperty() {
        this.propertyValue = null;
        this.propertyName = String.valueOf(numberOfInstances++);
    }

    @Nullable
    public T getPropertyValue() {
        return propertyValue;
    }

    @NotNull
    public ObservableProperty<T> setPropertyValueWithoutNotifying(@Nullable final T propertyValue) {
        this.propertyValue = propertyValue;
        return this;
    }

    @NotNull
    public String getPropertyName() {
        return propertyName;
    }

    @NotNull
    public ObservableProperty<T> setPropertyValueAndFireIfPropertyChange(@Nullable final T propertyNewValue) {
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

    /**
     * @return true if property values are equal, compared with {@link Objects#equals(Object, Object)}.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObservableProperty<?> that = (ObservableProperty<?>) o;
        return Objects.equals(propertyValue, that.propertyValue);
    }

    @Override
    public int hashCode() {
        return propertyValue != null ? propertyValue.hashCode() : 0;
    }
}