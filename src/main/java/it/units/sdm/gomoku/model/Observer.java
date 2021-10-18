package it.units.sdm.gomoku.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public interface Observer extends PropertyChangeListener {

    void propertyChange(PropertyChangeEvent evt);

}