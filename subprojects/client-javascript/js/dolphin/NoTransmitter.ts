import cmd  = require("../../js/dolphin/Command")
import scmd = require("../../js/dolphin/SignalCommand")
import cc   = require("../../js/dolphin/ClientConnector")
export module dolphin {

    /**
     * A transmitter that is not transmitting at all.
     * It may serve as a stand-in when no real transmitter is needed.
     */

    export class NoTransmitter implements cc.dolphin.Transmitter {

        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void):void {

            // do nothing special

            onDone( [] );

        }

        signal(command:scmd.dolphin.SignalCommand) : void {
            // do nothing
        }

    }
}