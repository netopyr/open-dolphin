package org.opendolphin.core.client;

import org.opendolphin.core.Tag;

import java.util.Map;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public class ClientAttributeFactory {

    private ClientAttributeFactory() {
    }

    @Deprecated
    public static ClientAttribute create(String propertyName) {
        return new GClientAttribute(propertyName);
    }

    public static ClientAttribute create(String propertyName, Object initialValue, String qualifier, Tag tag) {
        return new GClientAttribute(propertyName, initialValue, qualifier, tag);
    }

    public static ClientAttribute create(String propertyName, Object initialValue, Tag tag) {
        return new GClientAttribute(propertyName, initialValue, null, tag);
    }

    public static ClientAttribute create(String propertyName, Object initialValue, String qualifier) {
        return new GClientAttribute(propertyName, initialValue, qualifier, Tag.VALUE);
    }

    public static ClientAttribute create(String propertyName, Object initialValue) {
        return new GClientAttribute(propertyName, initialValue, null, Tag.VALUE);
    }

    @Deprecated
    public static ClientAttribute create(Map props) {
        return new GClientAttribute(props);
    }

}
