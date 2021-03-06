package it.units.sdm.gomoku.mvvm_library;

import org.jetbrains.annotations.NotNull;

import java.beans.PropertyChangeSupport;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public interface Observable {

    static PropertyChangeSupport getSupportOf(@NotNull final Observable observable) {
        // support is "a list of listeners (~observers)" for a property (~observable)

        Supplier<PropertyChangeSupport> getSupportOrCreateIfNotExist = new Supplier<>() {
            private static final Map<Observable, PropertyChangeSupport> supports =
                    new ConcurrentHashMap<>();

            @Override
            public PropertyChangeSupport get() {
                PropertyChangeSupport support = supports.get(Objects.requireNonNull(observable));
                if (support == null) {
                    support = new PropertyChangeSupport(observable);
                    supports.put(observable, support);
                }
                return support;
            }
        };
        return getSupportOrCreateIfNotExist.get();
    }

    default void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        getSupportOf(this).firePropertyChange(propertyName, oldValue, newValue);
    }
}
