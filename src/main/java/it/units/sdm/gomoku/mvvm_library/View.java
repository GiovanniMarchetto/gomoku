package it.units.sdm.gomoku.mvvm_library;

import it.units.sdm.gomoku.property_change_handlers.PropertyObserver;
import it.units.sdm.gomoku.property_change_handlers.observable_properties.ObservableProperty;
import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class View<T extends Viewmodel> {

    @NotNull
    private final T viewmodelAssociatedWithView;
    @NotNull
    private final List<PropertyObserver<?>> propertiesObservedInViewModel;

    public View(@NotNull T viewmodelAssociatedWithView) {
        this.viewmodelAssociatedWithView = Objects.requireNonNull(viewmodelAssociatedWithView);
        this.propertiesObservedInViewModel = new ArrayList<>();
    }

    public @NotNull T getViewmodelAssociatedWithView() {
        return viewmodelAssociatedWithView;
    }

    public void onViewInitialized() {
    }

    public void onViewDisappearing() {
    }

    protected <S> void addObservedPropertyOfViewmodel(@NotNull final ObservableProperty<S> observedProperty,
                                                      @NotNull final Consumer<PropertyChangeEvent> actionOnPropertyChange) {
        propertiesObservedInViewModel.add(new PropertyObserver<>(
                Objects.requireNonNull(observedProperty), Objects.requireNonNull(actionOnPropertyChange)));
    }

    protected void stopObservingAllViewModelProperties() {
        propertiesObservedInViewModel.forEach(PropertyObserver::stopObserving);
        propertiesObservedInViewModel.clear();
    }
}