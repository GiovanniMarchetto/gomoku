package it.units.sdm.gomoku.mvvm_library;

import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public interface Observer extends PropertyChangeListener {

    default void observe(@NotNull final Observable observable) {
        Observable.getSupportOf(Objects.requireNonNull(observable)).addPropertyChangeListener(this);
        // TODO : a static map may be used to save if an Observer is already observing an Observer to avoid to re-invoke .addPropertyChangeListener(..) method (From documentation: The same listener object may be added more than once, and will be called as many times as it is added)
    }

    default void stopObserving(@NotNull final Observable observable) {
        Observable.getSupportOf(Objects.requireNonNull(observable)).removePropertyChangeListener(this);
    }

    void propertyChange(PropertyChangeEvent evt);

}