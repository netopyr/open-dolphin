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

package org.opendolphin.core.server

import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.ValueChangedCommand
import groovy.transform.CompileStatic

@CompileStatic
class ServerAttribute extends BaseAttribute {

    private boolean idAlreadySet = false;
    public  boolean notifyClient = true;

    ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier, Tag tag){
        super(propertyName, baseValue, qualifier, tag)
    }

    public void setId(long id) {
        if (idAlreadySet) {
            def pm = this.presentationModel
            throw new IllegalStateException("You can not set the id twice for attribute with id ${this.id} of presentation model with id ${pm?.id}.")
        }
        idAlreadySet = true;
        this.@id = id;
    }

    @Override /** casting for convenience */
    ServerPresentationModel getPresentationModel() {
        (ServerPresentationModel) super.getPresentationModel()
    }

    @Override
    void setValue(Object value) {
        super.setValue(value)
        if (notifyClient) {
            def response = presentationModel.modelStore.currentResponse
            ServerDolphin.changeValue(response, this, getValue())
            println "will notify client here"
        }
    }
}
