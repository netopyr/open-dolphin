import tsUnit = require("../../testsuite/tsUnit")
import cb     = require("../../js/dolphin/CommandBatcher")
import cc     = require("../../js/dolphin/ClientConnector")
import cmd    = require("../../js/dolphin/Command")
import vcc    = require("../../js/dolphin/ValueChangedCommand")


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

        blindFolding() {
            var cmd1    : vcc.dolphin.ValueChangedCommand = new vcc.dolphin.ValueChangedCommand(1, 0, 1);
            var cmd2    : vcc.dolphin.ValueChangedCommand = new vcc.dolphin.ValueChangedCommand(2, 0, 1); // other id, will be batched
            var cmd3    : vcc.dolphin.ValueChangedCommand = new vcc.dolphin.ValueChangedCommand(1, 1, 2); // will be folded

            var queue = [
                { command: cmd1, handler: null },
                { command: cmd2, handler: null },
                { command: cmd3, handler: null }
            ];
            var unfolded = queue[1];

            var batcher = new cb.dolphin.BlindCommandBatcher();

            var result = batcher.batch(queue);
            this.areIdentical( result.length, 2);

            this.areIdentical(result[0].command['attributeId'], 1);
            this.areIdentical(result[0].command['oldValue'],    0);
            this.areIdentical(result[0].command['newValue'],    2);
            this.areIdentical(result[1], unfolded);

            this.areIdentical( queue.length,  0);
        }
    }

}