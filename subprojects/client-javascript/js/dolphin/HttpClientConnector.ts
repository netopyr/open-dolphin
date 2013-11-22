import cmd = require("../../js/dolphin/Command")
import cc  = require("../../js/dolphin/ClientConnector")
export module dolphin {

    export class HttpClientConnector extends cc.dolphin.ClientConnector {

        http = new XMLHttpRequest();

        transmit(commands:cmd.dolphin.Command[], onDone: (result: cmd.dolphin.Command[]) => void  ) : void {

            this.http.onerror = (evt:ErrorEvent) => {
                alert(evt.message);
            }

            this.http.onloadend = (evt:ProgressEvent) => {

                alert("this alert should only come ones, but it sometimes comes twice! ")

                //onDone( [] ); // commented such we are not even progressing!
            }

            this.http.open('POST', 'http://localhost:8080/dolphin-grails/moreTime/index', true);
            this.http.send();

        }

    }
}