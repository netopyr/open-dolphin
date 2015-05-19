/// <reference path="ClientDolphin.ts"/>
/// <reference path="OpenDolphin.ts"/>

module opendolphin {

    export class DolphinBuilder {

        private url_: string;
        private reset_: boolean;
        private slackMS_ :number = 300;

        constructor(){

        }

        public url(url:string):DolphinBuilder {
            this.url_ = url;
            return this;
        }
        public reset(reset:boolean):DolphinBuilder {
            this.reset_ = reset;
            return this;
        }
        public slackMS(slackMS:number):DolphinBuilder {
            this.slackMS_ = slackMS;
            return this;
        }
        public build():ClientDolphin {
            return opendolphin.dolphin(this.url_, this.reset_, this.slackMS_);
        }
    }
}
