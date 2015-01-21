package org.opendolphin.core.client;

import java.util.List;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public class ClientPresentationModelFactory {

    private ClientPresentationModelFactory() {
    }

    public static ClientPresentationModel create(List<ClientAttribute> attributes) {
        return new GClientPresentationModel(attributes);
    }

    public static ClientPresentationModel create(String id, List<ClientAttribute> attributes) {
        return new GClientPresentationModel(id, attributes);
    }
}
