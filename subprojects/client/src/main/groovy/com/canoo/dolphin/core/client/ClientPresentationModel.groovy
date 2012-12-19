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

import com.canoo.dolphin.core.BasePresentationModel
import com.canoo.dolphin.core.Tag

// impls on client and server are different since client is setting the id

class ClientPresentationModel extends BasePresentationModel {

    ClientPresentationModel(List<ClientAttribute> attributes) {
        this(null, attributes)
    }

    ClientPresentationModel(String id, List<ClientAttribute> attributes) {
        super(id, attributes)
    }

    /** @deprecated use ClientDolphin.presentationModel */
    static ClientPresentationModel make(String id, List<String> attributeNames) {
        return ClientDolphin.presentationModel(id, attributeNames)
    }


    // override with server specific return values to avoid casting in client code

    ClientAttribute getAt(String propertyName) {
        return (ClientAttribute) super.getAt(propertyName)
    }

    ClientAttribute getAt(String propertyName, Tag tag) {
        return (ClientAttribute) super.getAt(propertyName, tag)
    }
}
