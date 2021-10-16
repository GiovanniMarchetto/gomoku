package it.units.sdm.gomoku.ui.gui.views;

import it.units.sdm.gomoku.model.Observable;
import it.units.sdm.gomoku.ui.gui.viewmodels.Viewmodel;

public abstract class View implements Observable {

    private final Viewmodel viewmodelAssociatedWithView;

    protected View(Viewmodel viewmodelAssociatedWithView) {
        this.viewmodelAssociatedWithView = viewmodelAssociatedWithView;
        getViewmodelAssociatedWithView().observe(this);                // bind viewModel -> view
    }

    public Viewmodel getViewmodelAssociatedWithView() {
        return viewmodelAssociatedWithView;
    }
}