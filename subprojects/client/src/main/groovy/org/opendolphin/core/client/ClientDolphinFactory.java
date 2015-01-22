package org.opendolphin.core.client;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public class ClientDolphinFactory {

    private ClientDolphinFactory() {}

    public static ClientDolphin create() {
        return new GClientDolphin();
    }
}
