package it.units.sdm.gomoku.mvvm_library;

import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;

public abstract class Viewmodel implements
        Observer   /* Viewmodel observes the Model*/,
        Observable /* Viewmodel is observed by the View */ {

    protected void runOnSeparateThread(@NotNull final Runnable runnable) {
        new Thread(runnable).start();
    }

    public abstract void propertyChange(PropertyChangeEvent evt);

    public void observe(Observable observable) {
        observable.beObservedBy(this);
    }

    public void stopObserving(Observable observable) {
        observable.stopBeingObservedBy(this);
    }
}