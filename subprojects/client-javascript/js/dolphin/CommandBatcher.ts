import cmd = require("../../js/dolphin/Command");
import cc  = require("../../js/dolphin/ClientConnector");

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
        batch(queue : cc.dolphin.CommandAndHandler[]) : cc.dolphin.CommandAndHandler[] {
            var result = [];
            this.processNext(queue, result);
            return result;
        }

        // recursive impl method to side-effect both queue and batch
        private processNext(queue : cc.dolphin.CommandAndHandler[], batch : cc.dolphin.CommandAndHandler[]) : void {
            if (queue.length < 1) return;
            var candidate = queue.shift();
            batch.push(candidate);
            if ( ! candidate.handler) { // handler null nor undefined: we have a blind
                this.processNext(queue, batch);
            }
        }
    }

}