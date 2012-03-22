package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.BaseAttribute

class ServerPresentationModel extends BasePresentationModel {

    ServerPresentationModel(List<BaseAttribute> attributes) {
        this(null, attributes)
    }

    ServerPresentationModel(String id, List<BaseAttribute> attributes) {
        super(id, attributes)
    }


}
