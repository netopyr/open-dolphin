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

    public PresentationModel findPresentationModelById(String id) {
        return getModelStore().findPresentationModelById(id);
    }

    public boolean linkExists(Link link) {
        return getModelStore().linkExists(link);
    }

    public void addModelStoreLinkListener(String linkType, ModelStoreLinkListener listener) {
        getModelStore().addModelStoreLinkListener(linkType, listener);
    }

    public void addModelStoreLinkListener(String linkType, Closure listener) {
        getModelStore().addModelStoreLinkListener(linkType, asType(listener, ModelStoreLinkListener.class));
    }

    public boolean unlink(Link link) {
        return getModelStore().unlink(link);
    }

    public boolean linkExists(PresentationModel start, PresentationModel end, String type) {
        return getModelStore().linkExists(start, end, type);
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

    public boolean unlink(PresentationModel model) {
        return getModelStore().unlink(model);
    }

    public boolean hasModelStoreListener(String presentationModelType, ModelStoreListener listener) {
        return getModelStore().hasModelStoreListener(presentationModelType, listener);
    }

    public List<Link> findAllLinksByModel(PresentationModel model) {
        return getModelStore().findAllLinksByModel(model);
    }

    public List<Link> findAllLinksByModelAndType(PresentationModel model, String type, Link.Direction direction) {
        return getModelStore().findAllLinksByModelAndType(model, type, direction);
    }

    public boolean hasModelStoreLinkListener(ModelStoreLinkListener listener) {
        return getModelStore().hasModelStoreLinkListener(listener);
    }

    public void removeModelStoreLinkListener(ModelStoreLinkListener listener) {
        getModelStore().removeModelStoreLinkListener(listener);
    }

    public List<Link> findAllLinksByModel(PresentationModel model, Link.Direction direction) {
        return getModelStore().findAllLinksByModel(model, direction);
    }

    public Link findLink(PresentationModel start, PresentationModel end, String type) {
        return getModelStore().findLink(start, end, type);
    }

    public boolean unlink(PresentationModel start, PresentationModel end, String type) {
        return getModelStore().unlink(start, end, type);
    }

    public List<Link> findAllLinksByModelAndType(PresentationModel model, String type) {
        return getModelStore().findAllLinksByModelAndType(model, type);
    }

    public void addModelStoreLinkListener(ModelStoreLinkListener listener) {
        getModelStore().addModelStoreLinkListener(listener);
    }

    public void addModelStoreLinkListener(Closure listener) {
        getModelStore().addModelStoreLinkListener(asType(listener, ModelStoreLinkListener.class));
    }

    public void addModelStoreListener(ModelStoreListener listener) {
        getModelStore().addModelStoreListener(listener);
    }

    public void addModelStoreListener(Closure listener) {
        getModelStore().addModelStoreListener(asType(listener, ModelStoreListener.class));
    }

    public boolean hasModelStoreLinkListener(String linkType, ModelStoreLinkListener listener) {
        return getModelStore().hasModelStoreLinkListener(linkType, listener);
    }

    public void removeModelStoreLinkListener(String linkType, ModelStoreLinkListener listener) {
        getModelStore().removeModelStoreLinkListener(linkType, listener);
    }

    public boolean link(PresentationModel start, PresentationModel end, String type) {
        return getModelStore().link(start, end, type);
    }
}
