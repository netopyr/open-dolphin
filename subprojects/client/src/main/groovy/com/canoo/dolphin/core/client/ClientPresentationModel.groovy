package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BasePresentationModel

// impls on client and server are different since client is setting the id

class ClientPresentationModel extends BasePresentationModel {

    ClientPresentationModel(List<ClientAttribute> attributes) {
        this(null, attributes)
    }

    ClientPresentationModel(String id, List<ClientAttribute> attributes) {
        super(id, attributes)
        for (ClientAttribute attribute in attributes) {
            attribute.communicator.registerAndSend this, attribute
        }
    }

    void addAttribute(ClientAttribute attribute){
        attributes << attribute
        // TODO refactor this call once the communicator property is removed from ClientAttribute
        attribute.communicator.registerAndSend this, attribute
    }
}
