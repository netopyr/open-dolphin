package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector;

public class JavaDemo {
    public static void main(String[] args) {
        InMemoryConfig inMemoryConfig = new InMemoryConfig();
        InMemoryClientConnector connector = (InMemoryClientConnector) inMemoryConfig.getConnector();
        connector.setProcessAsync(false);
        inMemoryConfig.withActions();
        inMemoryConfig.register(new JavaAction() );

        ConsoleView.show(connector);
    }
}


