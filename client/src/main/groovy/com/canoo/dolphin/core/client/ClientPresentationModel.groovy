package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BasePresentationModel

// impls on client and server are different since client is setting the id

class ClientPresentationModel extends BasePresentationModel {

    ClientPresentationModel(List<ClientAttribute> attributes) {
        this(null, attributes)
    }

    ClientPresentationModel(String id, List<ClientAttribute> attributes) {
        super(id, attributes)
        for (att in attributes) {
            att.communicator.registerAndSend id, this, att // todo: unregister on PCL unbound
        }
    }
}
