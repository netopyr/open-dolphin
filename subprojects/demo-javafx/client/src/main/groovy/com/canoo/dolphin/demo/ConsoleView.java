package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.client.comm.ClientConnector;
import com.canoo.dolphin.core.comm.NamedCommand;
import groovy.lang.Closure;

import java.util.Set;

public class ConsoleView {

    public static void show(final ClientConnector connector) {

        NamedCommand cmd = new NamedCommand();
        cmd.setId("javaAction");
        Closure callback = new Closure("") {
            public Object call(Set<String> pmIds) {
                System.out.println("pmIds = " + pmIds);
                String s = pmIds.iterator().next();
                PresentationModel pm = connector.getClientModelStore().findPresentationModelById(s);
                System.out.println("pm = " + pm);
                return null;
            }
        };
        connector.send(cmd, callback);
    }
}