package it.units.sdm.gomoku.mvvm_library;

import java.beans.PropertyChangeEvent;

public abstract class Viewmodel implements
        Observer   /* Viewmodel observes the Model*/,
        Observable /* Viewmodel is observed by the View */ {

    public abstract void propertyChange(PropertyChangeEvent evt);

    public void observe(Observable observable) {
        observable.addPropertyChangeListener(this);
    }

    public void stopObserving(Observable observable) {
        observable.removePropertyChangeListener(this);
    }
}