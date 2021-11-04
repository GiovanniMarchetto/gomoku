package it.units.sdm.gomoku.mvvm_library;

public abstract class View<T extends Viewmodel> implements Observable {  // TODO : should be observer? (some views implement Observer)

    private final T viewmodelAssociatedWithView;

    public View(T viewmodelAssociatedWithView) {
        this.viewmodelAssociatedWithView = viewmodelAssociatedWithView;
        getViewmodelAssociatedWithView().observe(this);                // bind viewModel -> view
    }

    public T getViewmodelAssociatedWithView() {
        return viewmodelAssociatedWithView;
    }

    public void onViewInitialized() {
    }
}