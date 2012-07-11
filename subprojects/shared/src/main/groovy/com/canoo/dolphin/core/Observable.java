package com.canoo.dolphin.core;

import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: aalmiray
 * Date: 7/11/12
 * Time: 11:25 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Observable {
    void addPropertyChangeListener(PropertyChangeListener listener);

    void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);

    void removePropertyChangeListener(PropertyChangeListener listener);

    void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);

    PropertyChangeListener[] getPropertyChangeListeners();

    PropertyChangeListener[] getPropertyChangeListeners(String propertyName);
}
