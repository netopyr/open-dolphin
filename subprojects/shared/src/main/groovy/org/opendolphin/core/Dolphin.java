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

import groovy.lang.Closure;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asType;

public abstract class Dolphin {
    public abstract ModelStore getModelStore();

    public Set<String> listPresentationModelIds() {
        return getModelStore().listPresentationModelIds();
    }

    public Collection<PresentationModel> listPresentationModels() {
        return getModelStore().listPresentationModels();
    }

    public List<PresentationModel> findAllPresentationModelsByType(String presentationModelType) {
        return getModelStore().findAllPresentationModelsByType(presentationModelType);
    }

    /** alias for findPresentationModelById */
    public PresentationModel getAt(String id) {
        return findPresentationModelById(id);
    }

    public PresentationModel findPresentationModelById(String id) {
        return getModelStore().findPresentationModelById(id);
    }

    public void removeModelStoreListener(ModelStoreListener listener) {
        getModelStore().removeModelStoreListener(listener);
    }

    public void removeModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        getModelStore().removeModelStoreListener(presentationModelType, listener);
    }

    public boolean hasModelStoreListener(ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(listener);
    }

    public void addModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        getModelStore().addModelStoreListener(presentationModelType, listener);
    }

    public void addModelStoreListener(String presentationModelType, Closure listener) {
        getModelStore().addModelStoreListener(presentationModelType, asType(listener, ModelStoreListener.class));
    }


    public boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(presentationModelType, listener);
    }


    public void addModelStoreListener(ModelStoreListener listener) {
        getModelStore().addModelStoreListener(listener);
    }

    public void addModelStoreListener(Closure listener) {
        getModelStore().addModelStoreListener(asType(listener, ModelStoreListener.class));
    }

    /**
     * For every attribute in the given presentation model, proliferate the attribute value to
     * all attributes that bear the same qualifier.
     */
    // todo dk: not quite sure whether this should be called automatically in some handle() methods
    public void updateQualifiers(PresentationModel presentationModel) {
        for (Attribute attribute : presentationModel.getAttributes()) {
            if (null == attribute.getQualifier()) continue;
            if (attribute.getTag() != Tag.VALUE) continue;
            for (Attribute target : getModelStore().findAllAttributesByQualifier(attribute.getQualifier())) {
                if (target.getTag() != Tag.VALUE) continue;
                target.setValue(attribute.getValue());
            }
        }
    }
}
