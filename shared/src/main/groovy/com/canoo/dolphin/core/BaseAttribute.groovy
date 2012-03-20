package com.canoo.dolphin.core

import groovy.beans.Bindable

/**
 An BaseAttribute represents the (transient) value of a property of a backing bean (server) or its server counterpart (client).
 The value may be null as long as the BaseAttribute is used as a "placeholder".
 * */

class BaseAttribute {
    long id = System.identityHashCode(this)
    final String propertyName

    /** may be null **/
    @Bindable Object value

    /** @throws AssertionError if
     * a property of the given name cannot be read from
     * a bean of the given type
     * */
    BaseAttribute(String propertyName) {
        assert propertyName
        this.propertyName = propertyName
    }

    String toString() { "$id : $propertyName" }
    // more may come later

}

