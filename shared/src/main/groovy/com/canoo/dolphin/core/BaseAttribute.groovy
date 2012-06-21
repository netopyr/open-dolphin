package com.canoo.dolphin.core

import groovy.beans.Bindable

/**
 The value may be null as long as the BaseAttribute is used as a "placeholder".
*/

abstract class BaseAttribute {

    long id = System.identityHashCode(this) // todo: dk: has to change to tell client from server
    final String propertyName

    abstract Object getValue()
    abstract void setValue(Object value)

    BaseAttribute(String propertyName) {
        assert propertyName     // todo: think about using GContract for the precondition
        this.propertyName = propertyName
    }

    String toString() { "$id : $propertyName" }
    // more may come later

    void syncWith(BaseAttribute source) {
        assert source           // todo: think about using GContract for the precondition
        if (this.is(source)) return
        id = source.id
        setValue source.value // go through setter to make sure PCLs are triggered
    }
}
