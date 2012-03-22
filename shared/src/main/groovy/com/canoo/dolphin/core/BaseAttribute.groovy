package com.canoo.dolphin.core

import groovy.beans.Bindable

/**
 An BaseAttribute represents the (transient) value of a property of a backing bean (server) or its server counterpart (client).
 The value may be null as long as the BaseAttribute is used as a "placeholder".
 * */

class BaseAttribute {

    long id = System.identityHashCode(this) // todo: dk: has to change to tell client from server
    final String propertyName

    /** may be null **/
    @Bindable Object value

    BaseAttribute(String propertyName) {
        assert propertyName
        this.propertyName = propertyName
    }

    String toString() { "$id : $propertyName" }
    // more may come later

    void syncWith(BaseAttribute other) {
        if (this.id == other.id) return
        id = other.id
        setValue other.value // go through setter to make sure PCLs are triggered
    }

}

