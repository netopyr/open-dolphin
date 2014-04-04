package org.opendolphin.demo

import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.DataflowQueue
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.comm.SwitchPresentationModelCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.ServerAttribute
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

public class ChatterActions extends DolphinServerAction {

    public static final String CMD_INIT     = "chatter.init"
    public static final String CMD_POST     = "chatter.post"
    public static final String CMD_ON_PUSH  = "chatter.on.push"
    public static final String CMD_RELEASE  = "chatter.release"
    public static final String PM_ID_INPUT  = 'chatter.input'
    public static final String TYPE_POST    = 'chatter.type.post'
    public static final String ATTR_NAME    = "name"
    public static final String ATTR_MESSAGE = "message"
    public static final String ATTR_DATE    = "date"

    static final Agent history = new Agent<List<DTO>>([])

    static final AtomicInteger count = new AtomicInteger(0)
    int userId;
    int postCount = 0

    private EventBus chatterBus
    private final DataflowQueue chatQueue = new DataflowQueue()

    ChatterActions subscribedTo(EventBus chatterBus) {
        this.chatterBus = chatterBus
        chatterBus.subscribe(chatQueue)
        return this
    }

    protected void newPost(String name, List<Command> response) {
        def postId = postCount++
        String pmId = "$userId-$postId".toString()
        String now = new Date().format('dd.MM.yy HH:mm')
        def currentPost = new DTO(
            new Slot(ATTR_NAME,   name, "$pmId-$ATTR_NAME"),
            new Slot(ATTR_MESSAGE,  "", "$pmId-$ATTR_MESSAGE"),
            new Slot(ATTR_DATE,    now, "$pmId-$ATTR_DATE")
        )
        history.sendAndWait { List posts ->
            posts << currentPost
            if (posts.size() > 10) posts.remove(0)
        }
        chatterBus.publish(chatQueue, [type: "new", dto: currentPost])
        presentationModel(pmId, TYPE_POST, currentPost)
        response.add new SwitchPresentationModelCommand(pmId: PM_ID_INPUT, sourcePmId: pmId)
    }

    protected void updateHistory(ServerAttribute attribute) {
        history << { List<DTO> posts -> // todo dk: could be send and wait
            def slot   = posts*.slots.flatten().find { it.qualifier == attribute.qualifier }
            if (!slot) return
            slot.value = attribute.value
            slot.tag   = attribute.tag
        }
    }

    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_INIT, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                // display the old conversation that happened before we joined
                history.sendAndWait { List<DTO> posts ->
                    for(post in posts) presentationModel(null, TYPE_POST, post)
                }
                // we start with an initial open post
                userId = count.getAndIncrement()
                newPost("User-${userId} (please change)", response)
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
                def attr = getServerDolphin().findAttributeById(command.attributeId)
                if (attr && attr.presentationModel.id == PM_ID_INPUT && attr.qualifier) {
                    String toCheck = attr.value
                    String replaced = toCheck.replaceAll(/<(\/?\w)/, /&lt;\1/)
                    if (toCheck == replaced) {
                        updateHistory(attr)
                        chatterBus.publish(chatQueue, [type: "change", qualifier: attr.qualifier, value: attr.value])
                    } else {
                        attr.value = replaced
                    }
                }
            }
        })

        actionRegistry.register(CMD_POST, new CommandHandler<Command>() {
            public void handleCommand(Command command, List<Command> response) {
                def name = getServerDolphin().getAt(PM_ID_INPUT).getAt(ATTR_NAME).value
                newPost(name, response)
            }
        })

        actionRegistry.register(CMD_ON_PUSH) { NamedCommand command, List<Command> response ->
            Map post = chatQueue.getVal(60, TimeUnit.SECONDS)    // return all values
            while (null != post) {
                if (post.type == "new") {
                    presentationModel(null, TYPE_POST, post.dto)
                }
                if (post.type == "change") {
                    def attributes = getServerDolphin().findAllAttributesByQualifier(post.qualifier)
                    if (attributes) {
                        attributes.first().value = post.value
                    }
                }
                post = chatQueue.getVal(20, TimeUnit.MILLISECONDS)
            }
            return response
        }

    }
}

