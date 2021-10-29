package it.units.sdm.gomoku.mvvm_library;

public abstract class View implements Observable {  // TODO : should be observer? (some views implement Observer)

    private final Viewmodel viewmodelAssociatedWithView;

    public View(Viewmodel viewmodelAssociatedWithView) {
        this.viewmodelAssociatedWithView = viewmodelAssociatedWithView;
        getViewmodelAssociatedWithView().observe(this);                // bind viewModel -> view
    }

    public Viewmodel getViewmodelAssociatedWithView() {
        return viewmodelAssociatedWithView;
    }
}