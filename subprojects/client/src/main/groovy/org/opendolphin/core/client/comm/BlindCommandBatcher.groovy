package org.opendolphin.core.client.comm

import groovy.util.logging.Log
import groovyx.gpars.agent.Agent
import groovyx.gpars.dataflow.Dataflow
import org.opendolphin.core.comm.ValueChangedCommand

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
    /** when attribute x changes its value from 0 to 1 and then from 1 to 2, merge this into one change from 0 to 2 */
    boolean mergeValueChanges = false

    protected final inProcess               = new AtomicBoolean(false) // whether we started to batch up commands
    protected final deferralNeeded          = new AtomicBoolean(false) // whether we need to give commands the opportunity to enter the queue
    protected shallWeEvenTryToMerge         = false // do not even try if there is no value change cmd in the batch

    @Override
    void batch(CommandAndHandler commandWithHandler) {
        log.finest "batching $commandWithHandler.command with ${commandWithHandler.handler ? '' : 'out' } handler"
        agent << { List<CommandAndHandler> commandsAndHandlers ->
            commandsAndHandlers << commandWithHandler
        }
        if (commandWithHandler.isBatchable()) {
            deferralNeeded.set(true)
            if (inProcess.get()) return
            processDeferred()
        } else {
            processBatch()
        }
    }

    protected void processDeferred() {
        inProcess.set(true)
        Dataflow.task {
            def count = maxBatchSize        // never wait for more than those
            while (deferralNeeded.get() && count > 0) {
                count--
                deferralNeeded.set(false)
                sleep(deferMillis)          // while we sleep, new requests may have arrived that request deferral
            }
            processBatch()
            inProcess.set(false)
        }
    }

    protected void processBatch() {
        agent << {  List<CommandAndHandler> commandsAndHandlers ->
            def last = batchBlinds(commandsAndHandlers)  // always send leading blinds first
            if (last) {                         // we do have a trailing command with handler and batch it separately
                waitingBatches << [last]
            }
            if ( ! commandsAndHandlers.empty) {
                processBatch() // this is not so much like recursion, more like a trampoline
            }
        }
    }

    protected CommandAndHandler batchBlinds(List<CommandAndHandler> queue) {
        if(queue.empty) return
        List<CommandAndHandler> blindCommands = new LinkedList()
        int counter = maxBatchSize                      // we have to check again, since new ones may have arrived since last check
        def val = take(queue)
        shallWeEvenTryToMerge = false
        while (counter-- && val?.isBatchable()) {      // we do have a blind
            addToBlindsOrMerge(blindCommands, val)
            val = counter ? take(queue) : null
        }
        log.finest "batching ${blindCommands.size()} blinds"
        if (blindCommands) waitingBatches << blindCommands
        return val // may be null or a cwh that has a handler
    }

    protected void addToBlindsOrMerge(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if ( ! wasMerged(blindCommands,val)) {
            blindCommands << val
        }
    }

    protected boolean wasMerged(List<CommandAndHandler> blindCommands, CommandAndHandler val) {
        if ( ! mergeValueChanges)                                    return false
        if ( ! shallWeEvenTryToMerge )                               return false
        if (blindCommands.empty)                                     return false
        if (! val.command)                                           return false
        if (! val.command      instanceof ValueChangedCommand)       return false
        shallWeEvenTryToMerge = true

        def mergeable = blindCommands.find { cah ->                 // this has O(n*n) and can become costly
            cah.command != null &&
            cah.command instanceof ValueChangedCommand &&
            cah.command.attributeId == val.command.attributeId &&
            cah.command.newValue == val.command.oldValue
        }
        if (! mergeable) return false

        mergeable.command.newValue = val.command.newValue

        return true
    }

    protected CommandAndHandler take(List<CommandAndHandler> intern) {
        if (intern.empty) return null
   		return intern.remove(0)
   	}
}
