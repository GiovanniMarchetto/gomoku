package it.units.sdm.gomoku.mvvm_library.views;

import it.units.sdm.gomoku.mvvm_library.Observable;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;

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