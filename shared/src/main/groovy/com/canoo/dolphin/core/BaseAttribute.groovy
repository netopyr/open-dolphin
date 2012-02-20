package com.canoo.dolphin.core

import groovy.beans.Bindable
import java.beans.PropertyDescriptor
import java.beans.Introspector

/**
 An BaseAttribute represents the (transient) value of a property of a backing bean.
 The value may be null as long as the BaseAttribute is used as a "placeholder".
 **/

class BaseAttribute {

    private static final Object[] noArgs = [] as Object[]
    protected PropertyDescriptor desc

    final String propertyName
    final Class  beanType

    /** may be null **/
    def bean

    /** may be null **/
    @Bindable def value


    /** @throws AssertionError if
     * a property of the given name cannot be read from
     * a bean of the given type
     **/
    BaseAttribute(Class beanType, String propertyName) {
        assert beanType
        assert propertyName
        this.beanType = beanType
        this.propertyName = propertyName
        desc = Introspector.getBeanInfo(beanType).getPropertyDescriptors().find { it.name == propertyName }
        assert desc
    }

    /** When the backing bean changes, the value must be updated 
     *  and thus listeners be informed.
     */
    final void setBean(bean) {
        this.bean = bean
        if(null == bean) {
            setValue(null)
            return
        }
        setValue(desc.getReadMethod().invoke(bean,noArgs))
    }

    String toString() { "$beanType : $propertyName" }
    // more may come later

}

