import cmd  = require("../../js/dolphin/Command")
import scmd = require("../../js/dolphin/SignalCommand")
import cc   = require("../../js/dolphin/ClientConnector")
import cod  = require("../../js/dolphin/Codec")

export module dolphin {

    export class HttpTransmitter implements cc.dolphin.Transmitter {

        http:XMLHttpRequest;
        codec:cod.dolphin.Codec

        constructor(public url: string, reset: boolean = true) {
            this.http = new XMLHttpRequest();
//            this.http.withCredentials = true; // not supported in all browsers
            this.codec = new cod.dolphin.Codec();
            if (reset) {
                this.invalidate();
            }
        }

        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void):void {

            this.http.onerror = (evt:ErrorEvent) => {
                alert("could not fetch " + this.url + ", message: " + evt.message); // todo dk: make this injectable
                onDone([]);
            }

            this.http.onloadend = (evt:ProgressEvent) => {
                var responseText = this.http.responseText;
                var responseCommands = this.codec.decode(responseText);
                onDone(responseCommands);
            }

            this.http.open('POST', this.url, true);
            this.http.send(this.codec.encode(commands));

        }

        signal(command : scmd.dolphin.SignalCommand) {
            var sig = new XMLHttpRequest(); // the signal commands need an extra connection
            sig.open('POST', this.url, true);
            sig.send(this.codec.encode([command]));
        }

        invalidate() {
            this.http.open('POST', this.url + 'invalidate?', false);
            this.http.send();
        }

    }

}