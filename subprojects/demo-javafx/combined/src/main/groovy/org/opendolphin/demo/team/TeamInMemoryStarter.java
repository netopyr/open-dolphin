package org.opendolphin.demo.team;

import javafx.application.Application;
import org.opendolphin.core.NoModelStore;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.comm.BlindCommandBatcher;
import org.opendolphin.core.client.comm.HttpClientConnector;
import org.opendolphin.core.client.comm.JavaFXUiThreadHandler;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.demo.JavaFxInMemoryConfig;


public class TeamInMemoryStarter {
    public static void main(String[] args) throws Exception {

        EventBus teamBus = new EventBus();

        final JavaFxInMemoryConfig config = new JavaFxInMemoryConfig();
        ClientDolphin clientDolphin = config.getClientDolphin();
        ServerDolphin serverDolphin = config.getServerDolphin();
        serverDolphin.register(new TeamMemberActions().subscribedTo(teamBus));

        // for concurrent long-polls, we use a second dolphin
        final JavaFxInMemoryConfig pollConfig = new JavaFxInMemoryConfig();
        ClientDolphin pollerDolphin = pollConfig.getClientDolphin();
        pollerDolphin.setClientModelStore(new NoModelStore(pollerDolphin));
        ServerDolphin pollerServer = pollConfig.getServerDolphin();
        pollerServer.register(new TeamMemberActions().subscribedTo(teamBus));

        TeamApplication.clientDolphin = clientDolphin;
        TeamApplication.pollerDolphin = pollerDolphin;
        Application.launch(TeamApplication.class);
    }
}
