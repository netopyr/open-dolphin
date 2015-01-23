/*
 * Copyright 2012-2015 Canoo Engineering AG.
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

package org.opendolphin.core.client;

import org.opendolphin.core.Tag;

import java.util.Map;

public class ClientAttributeFactory {

    private ClientAttributeFactory() {
    }

    @Deprecated
    public static ClientAttribute create(String propertyName) {
        return new GClientAttribute(propertyName);
    }

    public static ClientAttribute create(String propertyName, Object initialValue, String qualifier, Tag tag) {
        return new GClientAttribute(propertyName, initialValue, qualifier, tag);
    }

    public static ClientAttribute create(String propertyName, Object initialValue, Tag tag) {
        return new GClientAttribute(propertyName, initialValue, null, tag);
    }

    public static ClientAttribute create(String propertyName, Object initialValue, String qualifier) {
        return new GClientAttribute(propertyName, initialValue, qualifier, Tag.VALUE);
    }

    public static ClientAttribute create(String propertyName, Object initialValue) {
        return new GClientAttribute(propertyName, initialValue, null, Tag.VALUE);
    }

    @Deprecated
    public static ClientAttribute create(Map props) {
        return new GClientAttribute(props);
    }

}
