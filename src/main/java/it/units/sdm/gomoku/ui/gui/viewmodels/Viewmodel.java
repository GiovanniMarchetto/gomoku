package it.units.sdm.gomoku.ui.gui.viewmodels;

import it.units.sdm.gomoku.model.Observable;
import it.units.sdm.gomoku.model.Observer;

import java.beans.PropertyChangeEvent;

/**
 * An instance of this class is an item of the Viewmodel.
 * A Viewmodel observes its bound Models (the Models send notifications when something change).
 * A Viewmodel is observed by its bound View items (the Viewmodel sends notification when something change).
 * In this way data binding between View and Viewmodel goal is achieved, furthermore the Viewmodel observes
 * the Model, hence the data in the View items are consistent with the data in the Model.
 * The Viewmodel is also responsible to update the model, due to a command received from a view item.
 */
public abstract class Viewmodel implements
        Observer   /* Viewmodel observes the Model*/,
        Observable /* Viewmodel is observed by the View */ {

    public abstract void propertyChange(PropertyChangeEvent evt);

    public void observe(Observable observable) {
        observable.addPropertyChangeListener(this);
    }
}