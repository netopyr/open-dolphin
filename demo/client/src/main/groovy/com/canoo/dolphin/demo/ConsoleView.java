package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.client.ClientPresentationModel;
import com.canoo.dolphin.core.client.comm.ClientConnector;
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector;
import com.canoo.dolphin.core.comm.NamedCommand;
import groovy.lang.Closure;

import java.util.List;
import java.util.Set;

public class ConsoleView {

    public static void show(final ClientConnector connector) {

        NamedCommand cmd = new NamedCommand();
        cmd.setId("javaAction");
        Closure callback = new Closure("") {
            public Object call(Set<String> pmIds) {
                System.out.println("pmIds = " + pmIds);
                String s = pmIds.iterator().next();
                ClientPresentationModel pm = connector.getClientModelStore().findPmById(s);
                System.out.println("pm = " + pm);
                return null;
            }
        };
        connector.send(cmd, callback);
    }
}