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
package org.opendolphin.core;

public class ModelStoreConfig {

    private int pmCapacity;
    private int typeCapacity;
    private int attributeCapacity;
    private int qualifierCapacity;

    public ModelStoreConfig() {
        this(1000, 40, 10000, 1000);
    }

    public ModelStoreConfig(int pmCapacity, int typeCapacity, int attributeCapacity, int qualifierCapacity) {
        this.pmCapacity = pmCapacity;
        this.typeCapacity = typeCapacity;
        this.attributeCapacity = attributeCapacity;
        this.qualifierCapacity = qualifierCapacity;
    }

    public int getPmCapacity() {
        return pmCapacity;
    }

    public int getTypeCapacity() {
        return typeCapacity;
    }

    public int getAttributeCapacity() {
        return attributeCapacity;
    }

    public int getQualifierCapacity() {
        return qualifierCapacity;
    }
}

