package com.canoo.dolphin.binding

import com.canoo.dolphin.core.PresentationModel
import java.beans.Introspector

@Immutable
class OfAble {
    String propName

    ToAble of(PresentationModel source) {
        new ToAble(source, propName)
    }

    PojoToAble of(Object source) {
        new PojoToAble(source, propName)
    }
}

class ToAble {
    final PresentationModel source
    final String sourcePropName

    ToAble(PresentationModel source, String sourcePropName) {
        this.source = source
        this.sourcePropName = sourcePropName
    }

    OtherOfAble to(String targetPropertyName) {
        new OtherOfAble(source, sourcePropName, targetPropertyName)
    }
}

class OtherOfAble {
    final PresentationModel source
    final String sourcePropName
    final String targetPropName

    OtherOfAble(PresentationModel source, String sourcePropName, String targetPropName) {
        this.source = source
        this.sourcePropName = sourcePropName
        this.targetPropName = targetPropName
    }

    void of(Object target) {
        def attribute = source.findAttributeByPropertyName(sourcePropName)
        if (!attribute) throw new IllegalArgumentException("there is no attribute for property name '$sourcePropName' in '${source.dump()}'")
        target[targetPropName] = attribute.value // set initial value
        def changeListener = new MyPropChangeListener(target, targetPropName)
        if (!(changeListener in attribute.getPropertyChangeListeners('value'))) { // don't add the listener twice
            attribute.addPropertyChangeListener('value', changeListener)
        }
    }
}

class PojoToAble {
    final Object source
    final String sourcePropName

    PojoToAble(Object source, String sourcePropName) {
        this.source = source
        this.sourcePropName = sourcePropName
    }

    PojoOtherOfAble to(String targetPropertyName) {
        new PojoOtherOfAble(source, sourcePropName, targetPropertyName)
    }
}

class PojoOtherOfAble {
    final Object source
    final String sourcePropName
    final String targetPropName

    PojoOtherOfAble(Object source, String sourcePropName, String targetPropName) {
        this.source = source
        this.sourcePropName = sourcePropName
        this.targetPropName = targetPropName
    }

    void of(Object target, Closure converter  = null) { // todo: remove the duplication
        def pd = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors().find { it.name == sourcePropName }
        if (!pd) throw new IllegalArgumentException("there is no property named '$sourcePropName' in '${source.dump()}'")
        def changeListener = new MyPropChangeListener(target, targetPropName, converter)
        target[targetPropName] = changeListener.convert(source[sourcePropName]) // set initial value
        if (!(changeListener in source.getPropertyChangeListeners(sourcePropName))) { // don't add the listener twice
            source.addPropertyChangeListener(sourcePropName, changeListener)
        }
    }
}
