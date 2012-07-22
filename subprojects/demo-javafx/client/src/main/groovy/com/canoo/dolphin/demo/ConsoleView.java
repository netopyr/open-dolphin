package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.client.Dolphin;
import com.canoo.dolphin.core.comm.NamedCommand;
import groovy.lang.Closure;

import java.util.List;
import java.util.Set;

public class ConsoleView {

    public static void show() {
        NamedCommand cmd = new NamedCommand();
        cmd.setId("javaAction");
        Closure callback = new Closure("") {
            public Object call(List<String> pmIds) {
                System.out.println("pmIds = " + pmIds);
                String s = pmIds.iterator().next();
                PresentationModel pm = Dolphin.getClientModelStore().findPresentationModelById(s);
                System.out.println("pm = " + pm);
                return null;
            }
        };
        Dolphin.getClientConnector().send(cmd, callback);
    }
}