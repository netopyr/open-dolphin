import cmd = require("../../js/dolphin/Command")
import cc  = require("../../js/dolphin/ClientConnector")
import cod = require("../../js/dolphin/Codec")

export module dolphin {

    export class HttpTransmitter implements cc.dolphin.Transmitter {

        http:XMLHttpRequest;
        codec:cod.dolphin.Codec

        constructor(public url: string) {
            this.http = new XMLHttpRequest();
            this.http.withCredentials = true;
            this.codec = new cod.dolphin.Codec();

            this.invalidate(); // ATM creating a new HttpTransmitter will invalidate the current session
        }

        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void):void {

            this.http.onerror = (evt:ErrorEvent) => {
                alert("could not fetch " + this.url + ", message: " + evt.message);
                onDone([]);
            }

            this.http.onloadend = (evt:ProgressEvent) => {
                var responseText = this.http.responseText;
                console.log("got: "+responseText);
                var responseCommands = this.codec.decode(responseText);
                onDone(responseCommands);
            }

            this.http.open('POST', this.url, true);
            this.http.send(this.codec.encode(commands));

        }

        invalidate() {
            this.http.open('POST', this.url + 'invalidate', true);
            this.http.send();
        }

    }

}