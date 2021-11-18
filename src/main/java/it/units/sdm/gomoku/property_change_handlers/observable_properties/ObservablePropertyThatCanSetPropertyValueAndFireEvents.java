package it.units.sdm.gomoku.property_change_handlers.observable_properties;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ObservablePropertyThatCanSetPropertyValueAndFireEvents<PropertyValueType> extends ObservableProperty<PropertyValueType> implements Cloneable {

    public ObservablePropertyThatCanSetPropertyValueAndFireEvents() {
        super();
    }

    public ObservablePropertyThatCanSetPropertyValueAndFireEvents(@NotNull final ObservablePropertyThatCanSetPropertyValueAndFireEvents<PropertyValueType> observablePropertyThatCanSetPropertyValueAndFireEvents) {
        super(Objects.requireNonNull(observablePropertyThatCanSetPropertyValueAndFireEvents));
    }

    @Override
    @NotNull
    public ObservableProperty<PropertyValueType> setPropertyValueWithoutNotifying(@Nullable final PropertyValueType newPropertyValue) {
        return super.setPropertyValueWithoutNotifying(newPropertyValue);
    }

    @Override
    @NotNull
    public String getPropertyName() {
        return super.getPropertyName();
    }

    @Override
    @NotNull
    public ObservableProperty<PropertyValueType> setPropertyValueAndFireIfPropertyChange(
            @Nullable final PropertyValueType newPropertyValue) {   // TODO : synchronized needed?
        return super.setPropertyValueAndFireIfPropertyChange(newPropertyValue);
    }

    @Override
    public ObservablePropertyThatCanSetPropertyValueAndFireEvents<PropertyValueType> clone() {
        return new ObservablePropertyThatCanSetPropertyValueAndFireEvents<>(this);
    }

}