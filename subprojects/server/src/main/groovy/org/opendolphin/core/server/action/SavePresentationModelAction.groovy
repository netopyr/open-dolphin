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

package org.opendolphin.core.server.action

import org.opendolphin.core.comm.SavePresentationModelCommand
import org.opendolphin.core.comm.SavedPresentationModelNotification
import org.opendolphin.core.server.comm.ActionRegistry
import groovy.util.logging.Log

/**
 * A common superclass rsp. a role model for server-side "save" actions.
 * Please note that such an action should add a
 * SavedPresentationModelNotification
 * to the response in order to rebase the initialValues on the client.
 */

@Log
class SavePresentationModelAction extends DolphinServerAction {

    void registerIn(ActionRegistry registry) {
        registry.register(SavePresentationModelCommand) { SavePresentationModelCommand command, response ->
            // subclasses may have application specific logic here
            log.finest "S: Saving presentation model '$command.pmId'"
            response << new SavedPresentationModelNotification(pmId: command.pmId)
        }
    }
}
