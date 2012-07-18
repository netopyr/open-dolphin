package com.canoo.dolphin.core.client;

import javafx.beans.property.SimpleObjectProperty;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

/**
 * <p>JavaFX property wrapper around an attribute.</p>
 */
public class ClientAttributeWrapper extends SimpleObjectProperty<Object> {
    private final WeakReference<ClientAttribute> attributeRef;
    private final String name;

    public ClientAttributeWrapper(ClientAttribute attribute) {
        this.attributeRef = new WeakReference<ClientAttribute>(attribute);
        // we cache the attribute's propertyName as the property's name
        // because the value does not change and we want to avoid
        // dealing with null values from WR
        this.name = attribute.getPropertyName();
        attribute.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                fireValueChangedEvent();
            }
        });
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void set(Object value) {
        ClientAttribute attribute = attributeRef.get();
        if (attribute != null) attribute.setValue(value);
    }

    @Override
    public Object get() {
        ClientAttribute attribute = attributeRef.get();
        return attribute != null ? attribute.getValue() : null;
    }
}
