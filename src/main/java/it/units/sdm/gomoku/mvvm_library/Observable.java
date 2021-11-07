package it.units.sdm.gomoku.mvvm_library;

import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public interface Observable {

    static PropertyChangeSupport getSupportOf(Observable observable) {

        Supplier<PropertyChangeSupport> getSupportOrCreateIfNotExist = new Supplier<>() {
            private static final Map<Observable, PropertyChangeSupport> supports =
                    new ConcurrentHashMap<>();

            @Override
            public PropertyChangeSupport get() {
                PropertyChangeSupport support = supports.get(observable);
                if (support == null) {
                    support = new PropertyChangeSupport(observable);
                    supports.put(observable, support);
                }
                return support;
            }
        };
        return getSupportOrCreateIfNotExist.get();
    }

    default void beObservedBy(Observer observer) {
        getSupportOf(this).addPropertyChangeListener(observer);
    }

    default void stopBeingObservedBy(Observer observer) {
        getSupportOf(this).removePropertyChangeListener(observer);
    }

    default void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getSupportOf(this).firePropertyChange(propertyName, oldValue, newValue);
    }

    default void firePropertyChange(String propertyName, Object newValue) {
        firePropertyChange(propertyName, null, newValue);
    }

}
