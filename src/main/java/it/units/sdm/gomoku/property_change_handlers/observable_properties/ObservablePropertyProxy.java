package it.units.sdm.gomoku.property_change_handlers.observable_properties;

import org.jetbrains.annotations.NotNull;

public class ObservablePropertyProxy<PropertyValueType> extends ObservableProperty<PropertyValueType> {

    public ObservablePropertyProxy(@NotNull final ObservableProperty<PropertyValueType> observableProperty) {
        super(observableProperty);
    }

}
