package it.units.sdm.gomoku.mvvm_library;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface Observer extends PropertyChangeListener {

    void propertyChange(PropertyChangeEvent evt);

}