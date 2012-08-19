package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.comm.ValueChangedCommand

@Mixin(DataMixin)
class ServerAttribute extends BaseAttribute {

    ServerAttribute(String propertyName) {
        this(propertyName, null)
    }

    ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    ServerAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.qualifier = props.qualifier
    }

    /** A value should never be set directly on the server.
     * Instead, a value change request is sent to the client.
     * See the readme for the reasoning behind this design.
     */
    ValueChangedCommand changeValueCommand(newValue){
        new ValueChangedCommand(attributeId: id, newValue: newValue, oldValue: value)
    }

}
