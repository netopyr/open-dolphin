package org.opendolphin.core.server;

import org.opendolphin.core.Tag;

import java.util.List;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public class ServerPresentationModelFactory {

    private ServerPresentationModelFactory() {
    }

    public static ServerAttribute create(String propertyName, Object initialValue) {
        return new GServerAttribute(propertyName, initialValue);
    }

    public static ServerAttribute create(String propertyName, Object baseValue, String qualifier, Tag tag) {
        return new GServerAttribute(propertyName, baseValue, qualifier, tag);
    }

    public static ServerPresentationModel create(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore) {
        return new GServerPresentationModel(id, attributes, serverModelStore);
    }

    public static ServerPresentationModel create(String id, List<ServerAttribute> attributes, ServerModelStore serverModelStore, String presentationModelType) {
        return new GServerPresentationModel(id, attributes, serverModelStore, presentationModelType);
    }
}
