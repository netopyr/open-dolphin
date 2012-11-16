/*
 * Java-Copy "startPushDemo.groovy", community contribution
 * @author  Guenter Paul, gp@guenterpaul.de
 * date:   07.11.2012
 */
package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.client.ClientDolphin;
import com.canoo.dolphin.core.server.ServerDolphin;

public class StartPushDemoJava {
    private StartPushDemoJava() { } // Schnickschnack :)

    public static void main(String[] args) {
        // my company name convention
        JavaFxInMemoryConfig lJavaFxInMemoryConfig = new JavaFxInMemoryConfig();
        ServerDolphin lServerDolphin = lJavaFxInMemoryConfig.getServerDolphin();
        ClientDolphin lClientDolphin = lJavaFxInMemoryConfig.getClientDolphin();

        lServerDolphin.getServerConnector().register(new CustomAction(lServerDolphin.getServerModelStore()));

        PushView.show(lClientDolphin);
    }
}
