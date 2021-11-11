package it.units.sdm.gomoku.mvvm_library;

import java.beans.PropertyChangeEvent;

public abstract class Viewmodel implements
        Observer   /* Viewmodel observes the Model*/,
        Observable /* Viewmodel is observed by the View */ {

    public abstract void propertyChange(PropertyChangeEvent evt);

}