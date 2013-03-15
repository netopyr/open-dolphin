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

import groovy.transform.CompileStatic
import groovy.util.logging.Log
import org.opendolphin.core.Dolphin
import org.opendolphin.core.ModelStore
import org.opendolphin.core.Tag
import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.CreatePresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.comm.ValueChangedCommand
import org.opendolphin.core.server.action.*
import org.opendolphin.core.server.comm.NamedCommandHandler
import org.opendolphin.core.server.comm.ServerConnector

/**
 * The main Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */

@CompileStatic
@Log
class ServerDolphin extends Dolphin {

    /** the server model store is unique per user session */
    final ModelStore serverModelStore

    /** the serverConnector is unique per user session */
    final ServerConnector serverConnector

    ServerDolphin(ModelStore serverModelStore, ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore
        this.serverConnector = serverConnector
    }

    ServerDolphin() {
        this(new ModelStore(), new ServerConnector())
    }

    @Override
    ModelStore getModelStore() {
        serverModelStore
    }

    void registerDefaultActions() {
        register new StoreValueChangeAction()
        register new StoreAttributeAction()
        register new CreatePresentationModelAction()
        register new SwitchPresentationModelAction()
        register new StoreInitialValueChangeAction()
        register new DeletePresentationModelAction()
        serverConnector.register new EmptyAction()
    }

    void register(DolphinServerAction action) {
        action.serverDolphin = this
        serverConnector.register(action)
    }

    /** groovy-friendly convenience method to register a named action */
    void action(String name, Closure logic) {
        def serverAction = new ClosureServerAction(name, logic)
        register(serverAction)
    }
    /** java-friendly convenience method to register a named action */
    void action(String name, NamedCommandHandler namedCommandHandler) {
        def serverAction = new NamedServerAction(name, namedCommandHandler)
        register(serverAction)
    }

    /** Convenience method to let Dolphin create a presentation model as specified by the DTO. */
    static void presentationModel(List<Command> response, String id, String presentationModelType, DTO dto){
        response << new CreatePresentationModelCommand(pmId: id, pmType: presentationModelType, attributes: dto.encodable())
    }

    /** Convenience method to let Dolphin create a
     *    <strong> client-side only </strong>
     *  presentation model as specified by the DTO. */
    static void clientSideModel(List<Command> response, String id, String presentationModelType, DTO dto){
        response << new CreatePresentationModelCommand(pmId: id, pmType: presentationModelType, attributes: dto.encodable(), clientSideOnly:true)
    }

    /**
     * Convenience method to change an attribute value on the server side.
     */
    static void changeValue(List<Command>response, ServerAttribute attribute, value){
        if (null == attribute) {
            log.severe("Cannot change value on a null attribute to '$value'")
            return
        }
        if (attribute.value == value) return // standard bean check
        response << new ValueChangedCommand(attributeId: attribute.id, newValue: value, oldValue: attribute.value)
    }

    /** Convenience method for the InitializeAttributeCommand */
    static void initAt(List<Command>response, String pmId, String propertyName, String qualifier, Object newValue = null, Tag tag = Tag.VALUE) {
        response << new InitializeAttributeCommand(pmId: pmId, propertyName: propertyName, qualifier: qualifier, newValue: newValue, tag: tag)
    }

    // overriding super methods with server-specific return types to avoid casting
    ServerPresentationModel getAt(String pmId) {
        (ServerPresentationModel) super.getAt(pmId)
    }

}
