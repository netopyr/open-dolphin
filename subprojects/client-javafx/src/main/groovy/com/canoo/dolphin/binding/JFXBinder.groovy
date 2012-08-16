package com.canoo.dolphin.binding

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import groovy.transform.Canonical
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class JFXBinder {
    static JFXBindOfAble bind(String sourcePropertyName) {
        new JFXBindOfAble(sourcePropertyName)
    }

    static BindPojoOfAble bindInfo(String sourcePropertyName) {
        Binder.bindInfo(sourcePropertyName)
    }

    static JFXUnbindOfAble unbind(String sourcePropertyName) {
        new JFXUnbindOfAble(sourcePropertyName)
    }

    static UnbindPojoOfAble unbindInfo(String sourcePropertyName) {
        Binder.unbindInfo(sourcePropertyName)
    }
}

@Immutable
class JFXUnbindOfAble {
    String sourcePropertyName

    JFXUnbindFromAble of(javafx.scene.Node source) {
        new JFXUnbindFromAble(source, sourcePropertyName)
    }

    UnbindFromAble of(PresentationModel source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }

    UnbindClientFromAble of(ClientPresentationModel source) {
        new UnbindClientFromAble(source.findAttributeByPropertyName(sourcePropertyName))
    }

    UnbindPojoFromAble of(Object source) {
        return Binder.unbind(sourcePropertyName).of(source)
    }
}

class JFXUnbindFromAble {
    final javafx.scene.Node source
    final String sourcePropertyName

    JFXUnbindFromAble(javafx.scene.Node source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    JFXUnbindOtherOfAble from(String targetPropertyName) {
        new JFXUnbindOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class UnbindClientFromAble {
    final ClientAttribute attribute

    UnbindClientFromAble(ClientAttribute attribute) {
        this.attribute = attribute
    }

    UnbindClientOtherOfAble from(String targetPropertyName) {
        new UnbindClientOtherOfAble(attribute, targetPropertyName)
    }
}

class JFXUnbindOtherOfAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final String targetPropertyName

    JFXUnbindOtherOfAble(javafx.scene.Node source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName)
        // blindly remove the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().removeListener(listener)
    }
}

class UnbindClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropertyName

    UnbindClientOtherOfAble(ClientAttribute attribute, String targetPropertyName) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.removePropertyChangeListener('value', listener)
    }
}


@Immutable
class JFXBindOfAble {
    String sourcePropertyName

    JFXBindToAble of(javafx.scene.Node source) {
        new JFXBindToAble(source, sourcePropertyName)
    }

    BindToAble of(PresentationModel source) {
        return Binder.bind(sourcePropertyName).of(source)
    }

    BindClientToAble    of(ClientPresentationModel source) {
        new BindClientToAble(source.findAttributeByPropertyName(sourcePropertyName))
    }

    BindPojoToAble of(Object source) {
        return Binder.bind(sourcePropertyName).of(source)
    }
}

class JFXBindToAble {
    final javafx.scene.Node source
    final String sourcePropertyName

    JFXBindToAble(javafx.scene.Node source, String sourcePropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
    }

    JFXBindOtherOfAble to(String targetPropertyName) {
        new JFXBindOtherOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class BindClientToAble {
    final ClientAttribute attribute

    BindClientToAble(ClientAttribute attribute) {
        this.attribute = attribute
    }

    BindClientOtherOfAble to(String targetPropertyName) {
        new BindClientOtherOfAble(attribute, targetPropertyName)
    }
}

class JFXBindOtherOfAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final String targetPropertyName

    JFXBindOtherOfAble(javafx.scene.Node source, String sourcePropertyName, String targetPropertyName) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target, Closure converter = null) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        // blindly add the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }
}

class BindClientOtherOfAble {
    final ClientAttribute attribute
    final String targetPropertyName

    BindClientOtherOfAble(ClientAttribute attribute, String targetPropertyName) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
    }

    void of(Object target, Closure converter = null) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }
}

@Canonical
class JFXBinderPropertyChangeListener implements PropertyChangeListener {
    Attribute attribute
    Object target
    String targetPropertyName
    Closure converter

    void update() {
        target[targetPropertyName] = convert(attribute.value)
    }

    void propertyChange(PropertyChangeEvent evt) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter(value) : value
    }

    @Override
    public boolean equals(Object o) {
        if (this.is(o)) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JFXBinderPropertyChangeListener that = (JFXBinderPropertyChangeListener) o;

        if (!attribute.equals(that.attribute)) return false;
        if (!target.equals(that.target)) return false;
        if (!targetPropertyName.equals(that.targetPropertyName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = attribute.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + targetPropertyName.hashCode();
        return result;
    }
}

@Canonical
class JFXBinderChangeListener implements ChangeListener {
    javafx.scene.Node source
    String sourcePropertyName
    Object target
    String targetPropertyName
    Closure converter

    void update() {
        if (target instanceof PresentationModel) {
            target[targetPropertyName].value = convert(source[sourcePropertyName])
        } else {
            target[targetPropertyName] = convert(source[sourcePropertyName])
        }
    }

    void changed(ObservableValue oe, Object oldValue, Object newValue) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter(value) : value
    }

    @Override
    public boolean equals(Object o) {
        if (this.is(o)) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JFXBinderChangeListener that = (JFXBinderChangeListener) o;

        if (!source.equals(that.source)) return false;
        if (!sourcePropertyName.equals(that.sourcePropertyName)) return false;
        if (!target.equals(that.target)) return false;
        if (!targetPropertyName.equals(that.targetPropertyName)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + sourcePropertyName.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + targetPropertyName.hashCode();
        return result;
    }
}