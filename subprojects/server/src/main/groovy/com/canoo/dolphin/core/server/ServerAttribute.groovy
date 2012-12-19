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

package com.canoo.dolphin.core.server

import com.canoo.dolphin.core.BaseAttribute
import com.canoo.dolphin.core.Tag
import com.canoo.dolphin.core.comm.ValueChangedCommand

class ServerAttribute extends BaseAttribute {
    ServerAttribute(String propertyName) {
        this(propertyName, null)
    }

    ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier, Tag tag){
        super(propertyName, baseValue, qualifier, tag)
    }

    ServerAttribute(Map props) {
        this(props.propertyName, props.initialValue)
        this.qualifier = props.qualifier
    }

    /** A value should never be set directly on the server.
     * Instead, a value change request is sent to the client.
     * See the readme for the reasoning behind this design.
     * @deprecated use ServerDolphin.changeValue
     */
    ValueChangedCommand changeValueCommand(newValue) {
        new ValueChangedCommand(attributeId: id, newValue: newValue, oldValue: value)
    }
}
