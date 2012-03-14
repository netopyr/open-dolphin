package com.canoo.dolphin.core

import groovy.beans.Bindable

/**
 An BaseAttribute represents the (transient) value of a property of a backing bean.
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

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        BaseAttribute that = (BaseAttribute) o

        if (id != that.id) return false

        return true
    }

    int hashCode() {
        return (int) (id ^ (id >>> 32))
    }
}

