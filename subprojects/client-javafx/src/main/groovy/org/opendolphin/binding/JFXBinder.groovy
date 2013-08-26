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
import org.opendolphin.core.client.ClientPresentationModel
import groovy.transform.Immutable
import groovy.transform.Canonical
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.logging.Level
import java.util.logging.Logger

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

    static UnbindInfoOfAble unbindInfo(String sourcePropertyName) {
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

    JFXUnbindTargetOfAble from(String targetPropertyName) {
        new JFXUnbindTargetOfAble(source, sourcePropertyName, targetPropertyName)
    }
}

class UnbindClientFromAble {
    final Attribute attribute

    UnbindClientFromAble(Attribute attribute) {
        this.attribute = attribute
    }

    UnbindClientTargetOfAble from(String targetPropertyName) {
        new UnbindClientTargetOfAble(attribute, targetPropertyName)
    }
}

class JFXUnbindTargetOfAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final String targetPropertyName

    JFXUnbindTargetOfAble(javafx.scene.Node source, String sourcePropertyName, String targetPropertyName) {
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

class UnbindClientTargetOfAble {
    final Attribute attribute
    final String targetPropertyName

    UnbindClientTargetOfAble(Attribute attribute, String targetPropertyName) {
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

    BindClientToAble of(ClientPresentationModel source) {
        new BindClientToAble(source.findAttributeByPropertyNameAndTag(sourcePropertyName, tag))
    }

    BindPojoToAble of(Object source) {
        return Binder.bind(sourcePropertyName, tag).of(source)
    }
}

class JFXBindToAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final Converter converter

    JFXBindToAble(javafx.scene.Node source, String sourcePropertyName, Converter converter = null) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.converter = converter
    }

    JFXBindTargetOfAble to(String targetPropertyName) {
        new JFXBindTargetOfAble(source, sourcePropertyName, targetPropertyName, converter)
    }

    JFXBindToAble using(Closure converter) {
        using new ConverterAdapter(converter)
    }

    JFXBindToAble using(Converter converter) {
        new JFXBindToAble(source, sourcePropertyName, converter)
    }
}

class BindClientToAble {
    final Attribute attribute
    final Converter converter

    BindClientToAble(Attribute attribute, Converter converter = null) {
        this.attribute = attribute
        this.converter = converter
    }

    BindClientTargetOfAble to(String targetPropertyName) {
        new BindClientTargetOfAble(attribute, targetPropertyName, converter)
    }

    BindClientToAble using(Closure converter) {
        using new ConverterAdapter(converter)
    }

    BindClientToAble using(Converter converter) {
        new BindClientToAble(attribute, converter)
    }
}

class JFXBindTargetOfAble {
    final javafx.scene.Node source
    final String sourcePropertyName
    final String targetPropertyName
    final Converter converter

    private static final Logger log  = Logger.getLogger(JFXBindTargetOfAble.class.getName())

    JFXBindTargetOfAble(javafx.scene.Node source, String sourcePropertyName, String targetPropertyName, Converter converter) {
        this.source = source
        this.sourcePropertyName = sourcePropertyName
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    void of(Object target) {
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        // blindly add the listener as Property does not expose a method to query existing listeners
        // javafx 2.2b17
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter)
    }
    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        if (log.isLoggable(Level.WARNING)) {
            log.warning("bind(<property>).of(<source>).to(<property>).of(<target>, <converter>) is deprecated! Please use: bind(<property>).of(<source>).using(<converter>).to(<property>).of(<target>)");
        }
        def listener = new JFXBinderChangeListener(source, sourcePropertyName, target, targetPropertyName, converter)
        source."${sourcePropertyName}Property"().addListener(listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }
}

class BindClientTargetOfAble {
    final Attribute attribute
    final String targetPropertyName
    final Converter converter

    private static final Logger log  = Logger.getLogger(BindClientTargetOfAble.class.getName())

    BindClientTargetOfAble(Attribute attribute, String targetPropertyName, Converter converter) {
        this.attribute = attribute
        this.targetPropertyName = targetPropertyName
        this.converter = converter
    }

    void of(Object target) {
        def listener = new JFXBinderPropertyChangeListener(attribute, target, targetPropertyName, converter)
        attribute.addPropertyChangeListener('value', listener)
        listener.update() // set the initial value after the binding and trigger the first notification
    }

    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Closure converter) {
        of target, new ConverterAdapter(converter)
    }
    @Deprecated // TODO (DOL-93) remove legacy code
    void of(Object target, Converter converter) {
        if (log.isLoggable(Level.WARNING)) {
            log.warning("bind(<property>).of(<source>).to(<property>).of(<target>, <converter>) is deprecated! Please use: bind(<property>).of(<source>).using(<converter>).to(<property>).of(<target>)");
        }
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