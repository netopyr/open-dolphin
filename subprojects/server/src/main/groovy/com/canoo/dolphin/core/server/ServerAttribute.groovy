package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute

class ServerAttribute extends BaseAttribute {
    ServerAttribute(String propertyName) {
        this(propertyName, null)
    }

    ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    ServerAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.dataId = props.dataId
    }
}
