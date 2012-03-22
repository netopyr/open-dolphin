package com.canoo.dolphin.core

import groovy.beans.Bindable

/**
 The value may be null as long as the BaseAttribute is used as a "placeholder".
*/

class BaseAttribute {

    long id = System.identityHashCode(this) // todo: dk: has to change to tell client from server
    final String propertyName

    /** may be null **/
    @Bindable Object value // setter contains really-changes-value check

    BaseAttribute(String propertyName) {
        assert propertyName
        this.propertyName = propertyName
    }

    String toString() { "$id : $propertyName" }
    // more may come later

    void syncWith(BaseAttribute source) {
        if (this.id == source.id) return
        id = source.id
        setValue source.value // go through setter to make sure PCLs are triggered
    }

}

