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

    void syncWith(ServerPresentationModel sourcePm, Closure onAttribute) {
        if (this.is(sourcePm)) return
        sourcePm.attributes.each { sourceAttribute ->
            def attribute = attributes.find { it.propertyName == sourceAttribute.propertyName }
            if (attribute.id == sourceAttribute.id) return
            onAttribute attribute, sourceAttribute
            attribute.syncWith sourceAttribute
        }
    }
}
