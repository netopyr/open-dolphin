package com.canoo.dolphin.core;

import com.canoo.dolphin.core.Observable;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class AbstractObservable implements Observable {
    private final PropertyChangeSupport pcs;

    public AbstractObservable() {
        pcs = new PropertyChangeSupport(this);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listener == null || containsListener(listener, getPropertyChangeListeners())) return;
        pcs.addPropertyChangeListener(listener);
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        if (listener == null || containsListener(listener, getPropertyChangeListeners(propertyName))) return;
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public PropertyChangeListener[] getPropertyChangeListeners(String propertyName) {
        return pcs.getPropertyChangeListeners(propertyName);
    }

    protected void firePropertyChange(PropertyChangeEvent event) {
        if (event != null && event.getOldValue() == event.getNewValue()) return;
        pcs.firePropertyChange(event);
    }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (oldValue == newValue) return;
        pcs.firePropertyChange(propertyName, oldValue, newValue);
    }

    private boolean containsListener(PropertyChangeListener listener, PropertyChangeListener[] listeners) {
        for (PropertyChangeListener subject : listeners) {
            if (subject == listener) return true;
        }
        return false;
    }
}