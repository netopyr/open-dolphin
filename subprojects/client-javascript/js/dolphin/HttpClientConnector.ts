import cmd = require("../../js/dolphin/Command")
import cc  = require("../../js/dolphin/ClientConnector")
export module dolphin {

    export class HttpClientConnector extends cc.dolphin.ClientConnector {

        transmit(commands:cmd.dolphin.Command[], onDone: (result: cmd.dolphin.Command[]) => void  ) : void {
            // do the XmlHttpRequest here


            // connect( ..., { ..., onDone(result) }, {...., onDone( null ) })

        }

    }
}