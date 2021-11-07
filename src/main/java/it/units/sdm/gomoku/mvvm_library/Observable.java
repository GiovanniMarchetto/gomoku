package it.units.sdm.gomoku.mvvm_library;

import java.beans.PropertyChangeListener;
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

    default void addPropertyChangeListener(PropertyChangeListener pcl) {    // TODO : rename in addObserver, which must take Observer parameter instead of PropertyChangeListener
        getSupportOf(this).addPropertyChangeListener(pcl);
    }

    default void removePropertyChangeListener(PropertyChangeListener pcl) {
        getSupportOf(this).removePropertyChangeListener(pcl);
    }

    default void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getSupportOf(this).firePropertyChange(propertyName, oldValue, newValue);
    }

    default void firePropertyChange(String propertyName, Object newValue) {
        firePropertyChange(propertyName, null, newValue);
    }

}
