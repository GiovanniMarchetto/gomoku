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

//    @Override
//    public int hashCode() {   // TODO : equal objects must have equal hash codes - IMPORTANT: this implementation break properties and slow VERY VERY VERY MUCH test execution
//        return propertyValue != null ? propertyValue.hashCode() : 0;
//    }
}