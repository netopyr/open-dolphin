import ca = require("../../js/dolphin/ClientAttribute");

export module dolphin {

    var presentationModelInstanceCount = 0;

    export class ClientPresentationModel {

        attributes : ca.dolphin.ClientAttribute[] = [];

        constructor(public id: string, public presentationModelType: string) {
            if (typeof id !== 'undefined') { // even an empty string is a valid id
                this.id = id;
            } else {
                this.id = (presentationModelInstanceCount++).toString();
            }
        }

        addAttribute(attribute: ca.dolphin.ClientAttribute) {
            this.attributes.push(attribute);
        }

    }
}