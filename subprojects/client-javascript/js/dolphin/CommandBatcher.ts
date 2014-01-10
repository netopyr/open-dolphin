import cmd = require("../../js/dolphin/Command");
import cc  = require("../../js/dolphin/ClientConnector");
import vcc = require("../../js/dolphin/ValueChangedCommand");
import nc  = require("../../js/dolphin/NamedCommand");
import en  = require("../../js/dolphin/EmptyNotification");

export module dolphin {

    export interface CommandBatcher {
        /** create a batch of commands from the queue and remove the batched commands from the queue */

        // adding to the queue was via push such that fifo reading needs to be via shift

        batch(queue : cc.dolphin.CommandAndHandler[]) : cc.dolphin.CommandAndHandler[];
    }

    /** A Batcher that does no batching but merely takes the first element of the queue as the single item in the batch */
    export class NoCommandBatcher implements CommandBatcher {
        batch(queue : cc.dolphin.CommandAndHandler[]) : cc.dolphin.CommandAndHandler[] {
            return [ queue.shift() ];
        }
    }

    /** A batcher that batches the blinds (commands with no callback) and optionally also folds value changes */
    export class BlindCommandBatcher implements CommandBatcher {

        /** folding: whether we should try folding ValueChangedCommands */
        constructor(public folding:boolean = true){}

        batch(queue : cc.dolphin.CommandAndHandler[]) : cc.dolphin.CommandAndHandler[] {
            var result = [];
            this.processNext(queue, result);
            return result;
        }

        // recursive impl method to side-effect both queue and batch
        private processNext(queue : cc.dolphin.CommandAndHandler[], batch : cc.dolphin.CommandAndHandler[]) : void {
            if (queue.length < 1) return;
            var candidate = queue.shift();

            if (this.folding && candidate.command instanceof vcc.dolphin.ValueChangedCommand && (!candidate.handler)) { // see whether we can merge
                var found  : vcc.dolphin.ValueChangedCommand = null;
                var canCmd : vcc.dolphin.ValueChangedCommand = <vcc.dolphin.ValueChangedCommand> candidate.command;
                for( var i = 0; i < batch.length && found == null; i++) { // a shame there is no "find" in TS
                    if (batch[i].command instanceof vcc.dolphin.ValueChangedCommand) {
                        var batchCmd : vcc.dolphin.ValueChangedCommand = <vcc.dolphin.ValueChangedCommand> batch[i].command;
                        if (canCmd.attributeId == batchCmd.attributeId && batchCmd.newValue == canCmd.oldValue) {
                            found = batchCmd;
                        }
                    }
                }
                if (found) {                            // yes, we can
                    found.newValue = canCmd.newValue;   // change existing value, do not batch
                } else {
                    batch.push(candidate);              // we cannot merge, so batch the candidate
                }
            } else {
                batch.push(candidate);
            }
            if ( ! candidate.handler &&                 // handler null nor undefined: we have a blind
                 ! (candidate.command['className'] == "org.opendolphin.core.comm.NamedCommand") &&     // and no unknown server side effect
                 ! (candidate.command['className'] == "org.opendolphin.core.comm.EmptyNotification")   // and no unknown client side effect
               ) {
                this.processNext(queue, batch);         // then we can proceed with batching
            }
        }
    }

}