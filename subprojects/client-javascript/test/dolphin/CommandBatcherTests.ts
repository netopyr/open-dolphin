import tsUnit = require("../../testsuite/tsUnit")
import cb     = require("../../js/dolphin/CommandBatcher")
import cc     = require("../../js/dolphin/ClientConnector")
import cmd    = require("../../js/dolphin/Command")


export module dolphin {

    export class CommandBatcherTests extends tsUnit.tsUnit.TestClass {

        noBatcherDoesNotBatch() {
            var whateverCommandAndHandler : cc.dolphin.CommandAndHandler = {command: null, handler: null};
            var queue = [ whateverCommandAndHandler, whateverCommandAndHandler, whateverCommandAndHandler ];

            var batcher = new cb.dolphin.NoCommandBatcher();

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);
            this.areIdentical( queue.length,  2);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);
            this.areIdentical( queue.length,  1);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);
            this.areIdentical( queue.length,  0);

        }

        simpleBlindBatching() {
            var whateverCommandAndHandler : cc.dolphin.CommandAndHandler = { command: null, handler: null };
            var queue = [ whateverCommandAndHandler, whateverCommandAndHandler, whateverCommandAndHandler ];

            var batcher = new cb.dolphin.BlindCommandBatcher();

            var result = batcher.batch(queue);

            this.areIdentical( result.length, 3);
            this.areIdentical( queue.length,  0);
        }

        blindBatchingWithNonBlind() {
            var blind   : cc.dolphin.CommandAndHandler = { command: null, handler: null };
            var finisher: cc.dolphin.OnFinishedHandler = { onFinished : null, onFinishedData: null };
            var handled : cc.dolphin.CommandAndHandler = { command: null, handler: finisher };

            var queue = [ handled, blind, blind, handled, blind, handled ]; // batch sizes 1, 3, 2

            var batcher = new cb.dolphin.BlindCommandBatcher();

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 1);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 3);

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 2);

            this.areIdentical(result[0], blind);  // make sure we have the right sequence
            this.areIdentical(result[1], handled);

            this.areIdentical( queue.length,  0);
        }
    }

}