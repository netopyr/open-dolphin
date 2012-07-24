package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.client.ClientPresentationModel;
import com.canoo.dolphin.core.client.Dolphin;
import com.canoo.dolphin.core.client.comm.OnFinishedHandler;
import com.canoo.dolphin.core.comm.NamedCommand;

import java.util.List;

public class ConsoleView {

    public static void show() {
        NamedCommand cmd = new NamedCommand();
        cmd.setId("javaAction");
        OnFinishedHandler callback = new OnFinishedHandler() {
            public void onFinished(List<ClientPresentationModel> pms) {
                ClientPresentationModel pm = pms.iterator().next();
                System.out.println("pm = " + pm);
            }
        };
        Dolphin.getClientConnector().send(cmd, callback);
    }
}