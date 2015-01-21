package org.opendolphin.core.server;

/**
 * Created by hendrikebbers on 20.01.15.
 */
public class ServerDolphinFactory {

    private ServerDolphinFactory() {}

    public static ServerDolphin create() {
        return new GServerDolphin();
    }

}
