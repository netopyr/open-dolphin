/// <reference path="Command.ts"/>
/// <reference path="SignalCommand.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="Codec.ts"/>

module opendolphin {

    export class HttpTransmitter implements Transmitter {

        http:XMLHttpRequest;
        sig:XMLHttpRequest; // for the signal command, which needs an extra connection
        codec:Codec


        HttpCodes = {
            finished: 4,
            success : 200
        }
        constructor(public url: string, reset: boolean = true, public charset: string = "UTF-8") {
            this.http = new XMLHttpRequest();
            this.sig  = new XMLHttpRequest();
//            this.http.withCredentials = true; // not supported in all browsers
            this.codec = new Codec();
            if (reset) {
                this.invalidate();
            }
        }

        transmit(commands:Command[], onDone:(result:Command[]) => void):void {

            this.http.onerror = (evt:ErrorEvent) => {
                alert("could not fetch " + this.url + ", message: " + evt.message); // todo dk: make this injectable
                onDone([]);
            }

            this.http.onreadystatechange= (evt:ProgressEvent) => {
                if (this.http.readyState == this.HttpCodes.finished){

                    if(this.http.status == this.HttpCodes.success)
                    {
                        var responseText = this.http.responseText;
                        var responseCommands = this.codec.decode(responseText);
                        onDone(responseCommands);
                    }
                    //todo ks: if status is not 200 then show error
                }
            }

            this.http.open('POST', this.url, true);
            this.http.overrideMimeType("application/json; charset=" + this.charset ); // todo make injectable
            this.http.send(this.codec.encode(commands));

        }

        signal(command : SignalCommand) {
            this.sig.open('POST', this.url, true);
            this.sig.send(this.codec.encode([command]));
        }

        invalidate() {
            this.http.open('POST', this.url + 'invalidate?', false);
            this.http.send();
        }

    }

}