package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute
import java.beans.Introspector
import java.beans.PropertyDescriptor

class ServerAttribute extends BaseAttribute {
    private static final Object[] NO_ARGS = new Object[0]

    final PropertyDescriptor propertyDescriptor
    final Class beanType

    /** may be null **/
    Object bean

    ServerAttribute(Class beanType, String propertyName) {
        super(propertyName)
        assert beanType
        this.beanType = beanType
        propertyDescriptor = Introspector.getBeanInfo(beanType).propertyDescriptors.find { it.name == propertyName }
        assert propertyDescriptor
    }

    /** When the backing bean changes, the value must be updated 
     *  and thus listeners be informed.
     */
    final void setBean(bean) {
        this.bean = bean
        if (null == bean) {
            setValue(null)
            return
        }
        setValue(propertyDescriptor.readMethod.invoke(bean, NO_ARGS))
    }
}
