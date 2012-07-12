package com.canoo.dolphin.binding

import com.canoo.dolphin.core.PresentationModel

@Immutable
class OfAble {
    String propName

    ToAble of(PresentationModel source) {
        new ToAble(source, propName)
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
