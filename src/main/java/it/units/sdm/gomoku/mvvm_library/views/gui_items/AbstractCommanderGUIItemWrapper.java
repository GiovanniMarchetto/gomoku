package it.units.sdm.gomoku.mvvm_library.views.gui_items;

import it.units.sdm.gomoku.mvvm_library.viewmodels.Viewmodel;
import it.units.sdm.gomoku.mvvm_library.views.View;
import javafx.scene.Node;

/**
 * An instance of this class represents a {@link GUIItemWrapper} which, if externally modified (e.g., by the user)
 * can be observed by the Viewmodel bounded with this instance
 * One or more item of the View is observed by its bound Viewmodel (the View sends commands that receive from the user to Viewmodel.
 * In this way, bidirectional binding between the {@link GUIItemWrapper} and the Viewmodel is achieved.
 */
public abstract class AbstractCommanderGUIItemWrapper<GUIItemType extends Node>
        extends GUIItemWrapper<GUIItemType> {

    private View containerView;

    protected AbstractCommanderGUIItemWrapper(View containerView, GUIItemType guiItem) {
        super(guiItem);
        setView(containerView);
    }

    protected AbstractCommanderGUIItemWrapper(View containerView, GUIItemType guiItem, Viewmodel viewmodel) {
        super(guiItem, viewmodel);
        setView(containerView);
    }

    private void setView(View containerView) {
        this.containerView = containerView;
    }

    protected void makeTheViewToFirePropertyChange(String propertyName, Object oldValue, Object newValue) {
        containerView.firePropertyChange(propertyName, oldValue, newValue);
    }

}
