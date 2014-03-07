package org.opendolphin.demo.team;

import org.opendolphin.core.comm.*;
import org.opendolphin.core.server.EventBus;
import org.opendolphin.core.server.action.ServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;

import static org.opendolphin.demo.team.TeamMemberConstants.*;

/**
 * The sole responsibility of the TeamBusRelease is to release any "push action" that
 * may currently listen to push events in this session such that commands that are waiting on
 * the client side can be sent.
 * Since it may run concurrently in the same session, it must not have access to any unsecured mutable shared state.
 * For that reason, it is _not_ extending DolphinServerAction.
 * Sharing the teamBus is safe.
 */
public class TeamBusRelease implements ServerAction {

    private EventBus teamBus;

    public TeamBusRelease(EventBus teamBus) {
        this.teamBus = teamBus;
    }

    @Override
    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_RELEASE, new CommandHandler<SignalCommand>() {
            @Override
            public void handleCommand(SignalCommand command, List<Command> response) {
                // seeing the response is ok but we should not see any serverDolphin here
                teamBus.publish(null, new TeamEvent("release", null));
            }
        });

    }
}


