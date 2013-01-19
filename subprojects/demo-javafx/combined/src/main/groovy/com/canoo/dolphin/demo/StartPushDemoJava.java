/*
 * Java-Copy "startPushDemo.groovy", community contribution
 * @author  Guenter Paul, gp@guenterpaul.de
 * date:   07.11.2012
 */
package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.client.ClientDolphin;
import com.canoo.dolphin.core.server.ServerDolphin;
import com.canoo.dolphin.core.server.action.DolphinServerAction;

public class StartPushDemoJava {
    private StartPushDemoJava() { } // Schnickschnack :)

    public static void main(String[] args) {
        // my company name convention
        JavaFxInMemoryConfig lJavaFxInMemoryConfig = new JavaFxInMemoryConfig();
        ServerDolphin lServerDolphin = lJavaFxInMemoryConfig.getServerDolphin();
        ClientDolphin lClientDolphin = lJavaFxInMemoryConfig.getClientDolphin();

        DolphinServerAction action = new VehiclePushActions();
        action.setServerDolphin(lServerDolphin);
        lServerDolphin.getServerConnector().register(action);

        PushView.show(lClientDolphin);
    }
}
