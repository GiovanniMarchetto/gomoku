package it.units.sdm.gomoku.mvvm_library.views.gui_items;

import it.units.sdm.gomoku.mvvm_library.Observer;
import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import javafx.scene.Node;

/**
 * An instance of this class is an item of the View.
 * An item of the View observes its bound Viewmodel (the Viewmodel sends notification when something change).
 * One or more item of the View is observed by its bound Viewmodel (the View sends commands that receive from the user to Viewmodel.
 * In this way data binding between View and Viewmodel goal is achieved (unidirectional binding from the View to the Viewmodel).
 */
public abstract class GUIItemWrapper<GUIItemT extends Node>
        implements Observer /*Each item in the view observes the Viewmodel */ {

    private final GUIItemT guiItem;

    protected GUIItemWrapper(GUIItemT guiItem) {
        this.guiItem = guiItem;
    }

    protected GUIItemWrapper(GUIItemT guiItem, Viewmodel viewmodel) {
        this(guiItem);
        observeViewmodel(viewmodel);
    }

    protected void observeViewmodel(Viewmodel viewmodel) {
        viewmodel.addPropertyChangeListener(this);
    }

    public GUIItemT getGUIItem() {
        return guiItem;
    }

}