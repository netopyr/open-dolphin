package org.opendolphin.core.client.comm

import groovy.util.logging.Log
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Dataflow

import java.util.concurrent.atomic.AtomicBoolean
/**
 * A command batcher that puts all commands in one packet that
 * have no onFinished handler attached (blind commands), which is the typical case
 * for value change and create presentation model commands
 * when synchronizing back to the server.
 */

@Log
class BlindCommandBatcher extends CommandBatcher {

    final protected Agent<LinkedList<CommandAndHandler>> agent = Agent.agent(new LinkedList<CommandAndHandler>())

    /** Time allowed to fill the queue before a batch is assembled */
    long deferMillis = 10
    /** Must be > 0*/
    int maxBatchSize = 100

    protected final inProcess = new AtomicBoolean(false)


    @Override
    void batch(CommandAndHandler commandWithHandler) {
        log.finest "batching $commandWithHandler.command with handler: $commandWithHandler.handler"
        agent << { List<CommandAndHandler> commandsAndHandlers ->
            commandsAndHandlers << commandWithHandler
        }
        if (commandWithHandler.handler) {
            processBatch()
        } else {
            if (inProcess.get()) return
            inProcess.set(true)
            Dataflow.task {
                sleep(deferMillis)
                processBatch()
            }
        }
    }

    protected void processBatch() {
        agent << {  List<CommandAndHandler> commandsAndHandlers ->
            def last = batchBlinds(commandsAndHandlers)  // always send leading blinds first
            if (last) {                         // we do have a trailing command with handler and batch it separately
                waitingBatches << [last]
            }
            if (commandsAndHandlers.empty) {
                inProcess.set(false)
            } else {
                processBatch() // this is not so much like recursion, more like a trampoline
            }
        }
    }

    protected CommandAndHandler batchBlinds(List<CommandAndHandler> queue) {
        if(queue.empty) return
        List<CommandAndHandler> blindCommands = new LinkedList()
        int counter = maxBatchSize
        def val = take(queue)
        while (counter-- && val && !val.handler) {
            blindCommands << val
            val = counter ? take(queue) : null
        }
        log.finest "batching ${blindCommands.size()} blinds"
        if (blindCommands) waitingBatches << blindCommands
        return val // may be null or a cwh that has a handler
    }

    protected CommandAndHandler take(List<CommandAndHandler> intern) {
        if (intern.empty) return null
   		return intern.remove(0)
   	}
}
