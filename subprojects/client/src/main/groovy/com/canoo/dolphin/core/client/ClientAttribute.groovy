/*
 * Copyright 2012 Canoo Engineering AG.
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

package com.canoo.dolphin.core.client

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.Tag

/**
 * A client side (remote) ClientAttribute is considered a remote representation of a ServerAttribute.
 * Changes to a remote ClientAttribute are sent to the server. This happens by using a dedicated
 * One can bind against a ClientAttribute in two ways
 * a) as a PropertyChangeListener
 * b) through the valueProperty() method for JavaFx
 */

class ClientAttribute extends BaseAttribute {
    ClientAttribute(String propertyName) {
        this(propertyName, null)
    }

    ClientAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    ClientAttribute(String propertyName, Object initialValue, Tag tag) {
        super(propertyName, initialValue, tag)
    }


    ClientAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.qualifier = props.qualifier
    }
}
