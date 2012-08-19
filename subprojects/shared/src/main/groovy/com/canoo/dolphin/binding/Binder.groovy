package com.canoo.dolphin.binding

import com.canoo.dolphin.core.PresentationModel

import java.beans.Introspector
import groovy.transform.Canonical
import java.beans.PropertyChangeListener
import java.beans.PropertyChangeEvent

class Binder {
    static BindOfAble bind(String sourcePropertyName) {
        new BindOfAble(sourcePropertyName)
    }

    static BindPojoOfAble bindInfo(String sourcePropertyName) {
        new BindPojoOfAble(sourcePropertyName)
    }

    static UnbindOfAble unbind(String sourcePropertyName) {
        new UnbindOfAble(sourcePropertyName)
    }

    static UnbindPojoOfAble unbindInfo(String sourcePropertyName) {
        new UnbindPojoOfAble(sourcePropertyName)
    }
}

@Immutable
class UnbindOfAble {
    String sourcePropertyName

    UnbindFromAble of(PresentationModel source) {
        new UnbindFromAble(source, sourcePropertyName)
    }

    UnbindPojoFromAble of(Object source) {
        new UnbindPojoFromAble(source, sourcePropertyName)
    }
}

class UnbindFromAble {
    final PresentationModel source
    final String sourcePropertyName

    UnbindFromAble(PresentationModel source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    UnbindOtherOfAble from(String targetPropertyName) {
        new UnbindOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class UnbindOtherOfAble {
    final PresentationModel source
    final String sourcePropertyName
    final String targetPropertyName

    UnbindOtherOfAble(PresentationModel source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        def attribute = source.findAttributeByPropertyName(sourcePropertyName)
        if (!attribute) throw new IllegalArgumentException("there is no attribute for property name '$sourcePropertyName' in '${source.dump()}'")
        // find a BinderPropertyChangeListener that matches
        def listener = attribute.getPropertyChangeListeners('value').find {
            it instanceof BinderPropertyChangeListener && it.target == target && it.targetPropertyName == targetPropertyName
        }
        // remove the listener; this operation is null safe
        attribute.removePropertyChangeListener('value', listener)
    }
}

@Immutable
class UnbindPojoOfAble {
    String sourcePropertyName

    UnbindPojoFromAble of(Object source) {
        new UnbindPojoFromAble(source, sourcePropertyName)
    }
}

class UnbindPojoFromAble {
    final Object source
    final String sourcePropertyName

    UnbindPojoFromAble(Object source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    UnbindPojoOtherOfAble from(String targetPropertyName) {
        new UnbindPojoOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class UnbindPojoOtherOfAble {
    final Object source
    final String sourcePropertyName
    final String targetPropertyName

    UnbindPojoOtherOfAble(Object source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        def pd = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors().find { it.name == sourcePropertyName }
        if (!pd) throw new IllegalArgumentException("there is no property named '$sourcePropertyName' in '${source.dump()}'")
        // find a BinderPropertyChangeListener that matches
        def listener = source.getPropertyChangeListeners('value').find {
            it instanceof BinderPropertyChangeListener && it.target == target && it.targetPropertyName == targetPropertyName
        }
        // remove the listener
        if (listener) source.removePropertyChangeListener('value', listener)
    }
}

@Immutable
class BindOfAble {
    String sourcePropertyName

    BindToAble of(PresentationModel source) {
        new BindToAble(source, sourcePropertyName)
    }

    BindPojoToAble of(Object source) {
        new BindPojoToAble(source, sourcePropertyName)
    }
}

class BindToAble {
    final PresentationModel source
    final String sourcePropertyName

    BindToAble(PresentationModel source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    BindOtherOfAble to(String targetPropertyName) {
        new BindOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class BindOtherOfAble {
    final PresentationModel source
    final String sourcePropertyName
    final String targetPropertyName

    BindOtherOfAble(PresentationModel source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target, Closure converter = null) {
        def attribute = source.findAttributeByPropertyName(sourcePropertyName)
        if (!attribute) throw new IllegalArgumentException("there is no attribute for property name '$sourcePropertyName' in '${source.dump()}'")
        def changeListener = new BinderPropertyChangeListener(target, targetPropertyName, converter)
        target[targetPropertyName] = changeListener.convert(attribute.value) // set initial value
        // adding a listener is null and duplicate safe
        attribute.addPropertyChangeListener('value', changeListener)
    }
}

@Immutable
class BindPojoOfAble {
    String sourcePropertyName

    BindPojoToAble of(Object source) {
        new BindPojoToAble(source, sourcePropertyName)
    }
}

class BindPojoToAble {
    final Object source
    final String sourcePropertyName

    BindPojoToAble(Object source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    BindPojoOtherOfAble to(String targetPropertyName) {
        new BindPojoOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class BindPojoOtherOfAble {
    final Object source
    final String sourcePropertyName
    final String targetPropertyName

    BindPojoOtherOfAble(Object source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target, Closure converter = null) { // todo: remove the duplication
        def pd = Introspector.getBeanInfo(source.getClass()).getPropertyDescriptors().find { it.name == sourcePropertyName }
        if (!pd) throw new IllegalArgumentException("there is no property named '$sourcePropertyName' in '${source.dump()}'")
        def changeListener = new BinderPropertyChangeListener(target, targetPropertyName, converter)
        target[targetPropertyName] = changeListener.convert(source[sourcePropertyName]) // set initial value
        if (!(changeListener in source.getPropertyChangeListeners(sourcePropertyName))) { // don't add the listener twice
            source.addPropertyChangeListener(sourcePropertyName, changeListener)
        }
    }
}

@Canonical
class BinderPropertyChangeListener implements PropertyChangeListener {
    Object target
    String targetPropertyName
    Closure converter

    void propertyChange(PropertyChangeEvent evt) {
        target[targetPropertyName] = convert(evt.newValue)
    }

    Object convert(Object value) {
        converter != null ? converter(value) : value
    }

    @Override
    public boolean equals(Object o) {
        if (this.is(o)) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BinderPropertyChangeListener that = (BinderPropertyChangeListener) o;

        if (!target.equals(that.target)) return false;
        if (!targetPropertyName.equals(that.targetPropertyName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = target.hashCode();
        result = 31 * result + targetPropertyName.hashCode();
        return result;
    }
}
