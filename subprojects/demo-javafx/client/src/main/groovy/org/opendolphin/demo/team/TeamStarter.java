package org.opendolphin.demo.team;

import javafx.application.Application;
import org.opendolphin.core.NoModelStore;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.BlindCommandBatcher;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler;
import org.opendolphin.core.comm.JsonCodec;

// todo: make url base configurable from command line

public class TeamStarter {
    public static void main(String[] args) throws Exception {

        final String servletUrl = "http://localhost:8080/dolphin-grails/dolphin/";

        ClientDolphin clientDolphin = new ClientDolphin();
        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
        BlindCommandBatcher batcher = new BlindCommandBatcher();
        batcher.setMergeValueChanges(true);
        batcher.setDeferMillis(42);
        HttpClientConnector connector = new HttpClientConnector(clientDolphin, batcher, servletUrl);
        final JsonCodec codec = new JsonCodec();
        connector.setCodec(codec);
        final JavaFXUiThreadHandler uiThreadHandler = new JavaFXUiThreadHandler();
        connector.setUiThreadHandler(uiThreadHandler);
        clientDolphin.setClientConnector(connector);

        // for concurrent long-polls, we use a second dolphin
        ClientDolphin pollerDolphin = new ClientDolphin();
        pollerDolphin.setClientModelStore(new NoModelStore(pollerDolphin));
        HttpClientConnector poller = new HttpClientConnector(pollerDolphin, servletUrl);
        poller.setCodec(codec);
        poller.setUiThreadHandler(uiThreadHandler);
        pollerDolphin.setClientConnector(poller);

        TeamApplication.clientDolphin = clientDolphin;
        TeamApplication.pollerDolphin = pollerDolphin;
        Application.launch(TeamApplication.class);
    }
}
