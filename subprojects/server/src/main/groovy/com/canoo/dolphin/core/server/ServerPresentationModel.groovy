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

import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.Tag
import groovy.transform.CompileStatic

@CompileStatic
class ServerPresentationModel extends BasePresentationModel {
    ServerPresentationModel(List<ServerAttribute> attributes) {
        this(null, attributes)
    }

    ServerPresentationModel(String id, List<ServerAttribute> attributes) {
        super(id, attributes)
    }

    // override with server specific return values to avoid casting in client code

    ServerAttribute getAt(String propertyName) {
        return (ServerAttribute) super.getAt(propertyName)
    }

    ServerAttribute getAt(String propertyName, Tag tag) {
        return (ServerAttribute) super.getAt(propertyName, tag)
    }
}
