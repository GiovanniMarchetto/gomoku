package it.units.sdm.gomoku.property_change_handlers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SettableObservableProperty<PropertyValueType> extends ObservableProperty<PropertyValueType> implements Cloneable {

    public SettableObservableProperty() {
        super();
    }

    public SettableObservableProperty(@NotNull final SettableObservableProperty<PropertyValueType> settableObservableProperty) {
        super(Objects.requireNonNull(settableObservableProperty));
    }

    @Override
    @NotNull
    public ObservableProperty<PropertyValueType> setPropertyValueWithoutNotifying(@Nullable final PropertyValueType propertyValue) {
        return super.setPropertyValueWithoutNotifying(propertyValue);
    }

    @Override
    @NotNull
    public String getPropertyName() {
        return super.getPropertyName();
    }

    @Override
    @NotNull
    public ObservableProperty<PropertyValueType> setPropertyValueAndFireIfPropertyChange(
            @Nullable final PropertyValueType propertyNewValue) {   // TODO : synchronized needed?
        return super.setPropertyValueAndFireIfPropertyChange(propertyNewValue);
    }

    @Override
    public SettableObservableProperty<PropertyValueType> clone() {
        return new SettableObservableProperty<>(this);
    }

}