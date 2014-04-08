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
import org.opendolphin.core.Tag
import groovy.transform.CompileStatic

@CompileStatic
class ServerAttribute extends BaseAttribute {

    private boolean notifyClient = true;

    ServerAttribute(String propertyName, Object initialValue) {
        super(propertyName, initialValue)
    }

    public ServerAttribute(String propertyName, Object baseValue, String qualifier, Tag tag){
        super(propertyName, baseValue, qualifier, tag)
    }

    @Override /** casting for convenience */
    ServerPresentationModel getPresentationModel() {
        (ServerPresentationModel) super.getPresentationModel()
    }

    @Override
    void setValue(Object value) {
        super.setValue(value)
        if (notifyClient) {
            ServerDolphin.changeValue(presentationModel.modelStore.currentResponse, this, getValue())
        }
    }

    @Override
    void reset() {
        super.reset()
        if (notifyClient) {
            ServerDolphin.reset(presentationModel.modelStore.currentResponse, this)
        }
    }

    @Override
    void rebase() {
        super.rebase()
        if (notifyClient) {
            ServerDolphin.rebase(presentationModel.modelStore.currentResponse, this)
        }
    }

    public String getOrigin(){
        return "S";
    }

    /** Do the applyChange without create commands that are sent to the client */
    public void silently(Runnable applyChange) {
        def temp = notifyClient
        notifyClient = false
        applyChange.run()
        notifyClient = temp
    }
}
