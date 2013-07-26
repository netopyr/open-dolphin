/*
 * Copyright 2012-2013 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opendolphin.binding

import org.opendolphin.core.Attribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel
import groovy.transform.Immutable
import groovy.transform.Canonical
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class JFXBinder {
    static JFXBindOfAble bind(String sourcePropertyName) {
        new JFXBindOfAble(sourcePropertyName, Tag.VALUE)
    }

    static JFXBindOfAble bind(String sourcePropertyName, Tag tag) {
        new JFXBindOfAble(sourcePropertyName, tag)
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
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName)
        attribute.removePropertyChangeListener('value', listener)
    }
}


@Immutable
class JFXBindOfAble {
    String sourcePropertyName
    Tag    tag

    JFXBindToAble of(javafx.scene.Node source) {
        new JFXBindToAble(source, sourcePropertyName)
    }

    BindToAble of(PresentationModel source) {
        return Binder.bind(sourcePropertyName, tag).of(source)
    }

    BindClientToAble    of(ClientPresentationModel source) {
        new BindClientToAble(source.findAttributeByPropertyNameAndTag(sourcePropertyName, tag))
    }

    BindPojoToAble of(Object source) {
        return Binder.bind(sourcePropertyName, tag).of(source)
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
        of target, converter == null ? null : new ConverterAdapter(converter)
    }
    void of(Object target, Converter converter) {
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
        of target, converter == null ? null : new ConverterAdapter(converter)
    }
    void of(Object target, Converter converter) {
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
    Converter converter

    void update() {
        target[targetPropertyName] = convert(attribute.value)
    }

    void propertyChange(PropertyChangeEvent evt) {
        update()
    }

    Object convert(Object value) {
        converter != null ? converter.convert(value) : value
    }
    // we have equals(o) and hashCode() from @Canonical
}

@Canonical
class JFXBinderChangeListener implements ChangeListener {
    javafx.scene.Node source
    String sourcePropertyName
    Object target
    String targetPropertyName
    Converter converter

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
        converter != null ? converter.convert(value) : value
    }

    // we have equals(o) and hashCode() from @Canonical

}