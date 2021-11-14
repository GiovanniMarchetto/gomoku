package it.units.sdm.gomoku.property_change_handlers;

import org.jetbrains.annotations.NotNull;

public class ProxyObservableProperty<PropertyValueType> extends ObservableProperty<PropertyValueType> {  // TODO: test

    public ProxyObservableProperty(@NotNull final ObservableProperty<PropertyValueType> observableProperty) {
        super(observableProperty);
    }

}
