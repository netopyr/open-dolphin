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

package com.canoo.dolphin.core;

public class ModelStoreEvent {
    public enum Type {
        ADDED, REMOVED
    }

    private final Type type;
    private final PresentationModel presentationModel;

    public ModelStoreEvent(Type eventType, PresentationModel presentationModel) {
        this.type = eventType;
        this.presentationModel = presentationModel;
    }

    public Type getType() {
        return type;
    }

    public PresentationModel getPresentationModel() {
        return presentationModel;
    }

    public String toString() {
        return new StringBuilder()
                .append("PresentationModel ")
                .append(type == Type.ADDED ? "ADDED" : "REMOVED")
                .append(" ")
                .append(presentationModel.getId())
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelStoreEvent that = (ModelStoreEvent) o;

        if (type != that.type) return false;
        if (presentationModel != null ? !presentationModel.equals(that.presentationModel) : that.presentationModel != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (presentationModel != null ? presentationModel.hashCode() : 0);
        return result;
    }
}
