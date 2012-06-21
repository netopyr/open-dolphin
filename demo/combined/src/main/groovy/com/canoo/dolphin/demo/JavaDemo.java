package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.client.comm.InMemoryClientConnector;
import groovy.lang.Closure;

import java.util.concurrent.CountDownLatch;

public class JavaDemo {
    public static void main(String[] args) {
        final CountDownLatch latch = new CountDownLatch(1);
        InMemoryConfig inMemoryConfig = new InMemoryConfig();
        InMemoryClientConnector connector = (InMemoryClientConnector) inMemoryConfig.getConnector();

        connector.setHowToProcessInsideUI(new Closure("") {
            public Object call(final Closure whatToDoInside) {
                System.out.println("going inside ui");
                // do inside the UI thread:
                whatToDoInside.call();
                latch.countDown();
                return null;
            }
        });

        inMemoryConfig.withActions();
        inMemoryConfig.register(new JavaAction());

        ConsoleView.show(connector);
        System.out.println("waiting to finish");
        try { latch.await(); } catch (InterruptedException e) { e.printStackTrace();  }
        }
}


