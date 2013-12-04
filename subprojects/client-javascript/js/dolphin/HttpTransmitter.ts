import cmd = require("../../js/dolphin/Command")
import cc  = require("../../js/dolphin/ClientConnector")
export module dolphin {

    export class HttpTransmitter implements cc.dolphin.Transmitter {

        http = new XMLHttpRequest();

        transmit(commands:cmd.dolphin.Command[], onDone:(result:cmd.dolphin.Command[]) => void):void {

            this.http.onerror = (evt:ErrorEvent) => {
                //  alert("could not fetch http://localhost:8080/dolphin-grails/moreTime/index " + evt.message);
            }

            this.http.onloadend = (evt:ProgressEvent) => {
                console.log("transmission ended")
                onDone([]);
            }

            this.http.open('POST', 'http://localhost:8080/dolphin-grails/dolphin/', true);
            this.http.send();

        }

    }
}