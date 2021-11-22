package it.units.sdm.gomoku.mvvm_library;

import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

public interface Observer extends PropertyChangeListener {

    default void observe(@NotNull final Observable observable) {
        Observable.getSupportOf(Objects.requireNonNull(observable)).addPropertyChangeListener(this);
    }

    default void stopObserving(@NotNull final Observable observable) {
        Observable.getSupportOf(Objects.requireNonNull(observable)).removePropertyChangeListener(this);
    }

    void propertyChange(PropertyChangeEvent evt);

}