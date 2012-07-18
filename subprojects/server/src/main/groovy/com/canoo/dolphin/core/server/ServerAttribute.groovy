package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute

class ServerAttribute extends BaseAttribute {
    ServerAttribute(String propertyName) {
        super(propertyName)
    }

    ServerAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.dataId = props.dataId
    }
}
