package org.opendolphin.demo

import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.ServerPresentationModel
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

public class ChatterActions extends DolphinServerAction {

    public static final String CMD_INIT     = "chatter.init"
    public static final String CMD_POST     = "chatter.post"
    public static final String CMD_POLL     = "chatter.poll"
    public static final String PM_ID_INPUT  = 'chatter.input'
    public static final String TYPE_POST    = 'chatter.type.post'
    public static final String ATTR_NAME    = "name"
    public static final String ATTR_MESSAGE = "message"
    public static final String ATTR_ID      = "id"

    static final AtomicInteger count = new AtomicInteger(0);

    private EventBus chatterBus
    private final DataflowQueue chatQueue = new DataflowQueue()

    ChatterActions subscribedTo(EventBus chatterBus) {
        this.chatterBus = chatterBus
        chatterBus.subscribe(chatQueue)
        return this
    }

    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_INIT, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                def pm = getServerDolphin()[PM_ID_INPUT]
                def id = count.getAndIncrement()
                changeValue pm[ATTR_ID], id
                changeValue pm[ATTR_NAME], "User-$id (please change)"
            }
        })

        actionRegistry.register(CMD_POST, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {

                final ServerPresentationModel inputPM = getServerDolphin()[PM_ID_INPUT]
                final ServerAttribute nameAtt    = inputPM[ATTR_NAME]
                final ServerAttribute messageAtt = inputPM[ATTR_MESSAGE]

                // add the post to collection of known posts
                def post = new DTO(
                    new Slot(ATTR_NAME, nameAtt.value),
                    new Slot(ATTR_MESSAGE, messageAtt.value)
                )
                presentationModel(null, TYPE_POST, post)
                chatterBus.publish(chatQueue, post)

                // make sure the collection does not grow overly long
                def posts = getServerDolphin().findAllPresentationModelsByType(TYPE_POST)
                if (posts.size() > 10) {
                    getServerDolphin().delete response, posts.first()
                }

                // set back message input to empty string
                changeValue messageAtt, ""
            }
        })

        actionRegistry.register(CMD_POLL) { NamedCommand command, response ->
            DTO post = chatQueue.getVal(1, TimeUnit.SECONDS)    // return all values
            while (null != post) {
                // add the post to collection of known posts
                presentationModel(null, TYPE_POST, post)
                post = chatQueue.getVal(20, TimeUnit.MILLISECONDS)
            }

            return response
        }


    }

}

