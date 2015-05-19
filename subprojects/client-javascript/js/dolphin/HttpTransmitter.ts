/// <reference path="Command.ts"/>
/// <reference path="SignalCommand.ts"/>
/// <reference path="ClientConnector.ts"/>
/// <reference path="Codec.ts"/>

module opendolphin {

    export class HttpTransmitter implements Transmitter {

        http:XMLHttpRequest;
        sig:XMLHttpRequest; // for the signal command, which needs an extra connection
        codec:Codec;


        HttpCodes = {
            finished: 4,
            success : 200
        };
        constructor(public url: string, reset: boolean = true, public charset: string = "UTF-8") {
            this.http = new XMLHttpRequest();
            this.sig  = new XMLHttpRequest();
            if ("withCredentials" in this.http) { // browser supports CORS
                this.http.withCredentials = true; // NOTE: doing this for non CORS requests has no impact
                this.sig.withCredentials = true;
            }
            // NOTE: Browser might support CORS partially so we simply try to use 'this.http' for CORS requests instead of forbidding it
            // NOTE: XDomainRequest for IE 8, IE 9 not supported by dolphin because XDomainRequest does not support cookies in CORS requests (which are needed for the JSESSIONID cookie)

            this.codec = new Codec();
            if (reset) {
                this.invalidate();
            }
        }

        transmit(commands:Command[], onDone:(result:Command[]) => void, errorHandler:(any) => void):void {

            this.http.onerror = (evt:ErrorEvent) => {
                errorHandler({url: this.url, cause: evt});
                onDone([]);
            };

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
            };

            this.http.open('POST', this.url, true);
            if ("overrideMimeType" in this.http) {
                this.http.overrideMimeType("application/json; charset=" + this.charset ); // todo make injectable
            }
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