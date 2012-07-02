package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BasePresentationModel

class ServerPresentationModel extends BasePresentationModel {

    ServerPresentationModel(List<ServerAttribute> attributes) {
        this(null, attributes)
    }

    ServerPresentationModel(String id, List<ServerAttribute> attributes) {
        super(id, attributes)
    }
}
