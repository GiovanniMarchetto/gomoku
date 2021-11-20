package it.units.sdm.gomoku.property_change_handlers.observable_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ObservablePropertySettable<PropertyValueType> extends ObservableProperty<PropertyValueType> implements Cloneable {

    public ObservablePropertySettable() {
        super();
    }

    public ObservablePropertySettable(@Nullable final PropertyValueType initialValue) {
        super(initialValue);
    }

    public ObservablePropertySettable(@NotNull final ObservablePropertySettable<PropertyValueType> observablePropertySettable) {
        super(Objects.requireNonNull(observablePropertySettable));
    }

    @Override
    @NotNull
    public String getPropertyName() {
        return super.getPropertyName();
    }

    @Override
    @NotNull
    public ObservableProperty<PropertyValueType> setPropertyValue(
            @Nullable final PropertyValueType newPropertyValue) {   // TODO : synchronized needed?
        return super.setPropertyValue(newPropertyValue);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public ObservablePropertySettable<PropertyValueType> clone() {
        return new ObservablePropertySettable<>(this);
    }

}