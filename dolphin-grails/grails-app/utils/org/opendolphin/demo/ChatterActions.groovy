package org.opendolphin.demo

import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.SwitchPresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.EventBus
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

    static final AtomicInteger count = new AtomicInteger(0)
    int postCount = 0

    private EventBus chatterBus
    private final DataflowQueue chatQueue = new DataflowQueue()

    ChatterActions subscribedTo(EventBus chatterBus) {
        this.chatterBus = chatterBus
        chatterBus.subscribe(chatQueue)
        return this
    }

    protected void newPost(int userId, String name, List<Command> response) {
        def postId = postCount++
        def currentPost = new DTO(
            new Slot(ATTR_ID, userId),
            new Slot(ATTR_NAME, name,  "$userId-$postId-$ATTR_NAME"),
            new Slot(ATTR_MESSAGE, "", "$userId-$postId-$ATTR_MESSAGE")
        )
        chatterBus.publish(chatQueue, [type: "new", dto: currentPost])
        presentationModel("$userId-$postId", TYPE_POST, currentPost)
        response.add new SwitchPresentationModelCommand(pmId: PM_ID_INPUT, sourcePmId: "$userId-$postId")
    }

    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_INIT, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                final userId = count.getAndIncrement()
                newPost(userId, "User-${userId} (please change)", response)
            }
        })

        actionRegistry.register(CreatePresentationModelCommand, new CommandHandler<CreatePresentationModelCommand>() {
            public void handleCommand(CreatePresentationModelCommand command, List<Command> response) {
                // make sure the collection does not grow overly long
                def posts = getServerDolphin().findAllPresentationModelsByType(TYPE_POST)
                if (posts.size() > 10) {
                   getServerDolphin().delete response, posts.first()
                }
            }
        })

        actionRegistry.register(ValueChangedCommand, new CommandHandler<ValueChangedCommand>() {
            public void handleCommand(ValueChangedCommand command, List<Command> response) {
                def attr = getServerDolphin().getModelStore().findAttributeById(command.attributeId)
                if (attr && attr.presentationModel.id == PM_ID_INPUT && attr.qualifier) {
                    chatterBus.publish(chatQueue, [type: "change", qualifier: attr.qualifier, value: attr.value])
                }
            }
        })

        actionRegistry.register(CMD_POST, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                def name = getServerDolphin().getAt(PM_ID_INPUT).getAt(ATTR_NAME).value
                newPost(count.get(), name, response)
            }
        })

        actionRegistry.register(CMD_POLL) { NamedCommand command, List<Command> response ->
            Map post = chatQueue.getVal(10, TimeUnit.SECONDS)    // return all values
            while (null != post) {
                println post
                if (post.type == "new") {
                    presentationModel(null, TYPE_POST, post.dto)
                }
                if (post.type == "change") {
                    def attributes = getServerDolphin().getModelStore().findAllAttributesByQualifier(post.qualifier)
                    if (attributes) {
                        changeValue attributes.first(), post.value
                    }
                }
                post = chatQueue.getVal(20, TimeUnit.MILLISECONDS)
            }
            return response
        }

    }
}

