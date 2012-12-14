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

package com.canoo.dolphin.core.server.comm

import com.canoo.dolphin.core.comm.Codec
import com.canoo.dolphin.core.comm.Command
import com.canoo.dolphin.core.server.action.*
import groovy.util.logging.Log
import org.codehaus.groovy.runtime.StackTraceUtils
import java.util.logging.Level

@Log
class ServerConnector {
    Codec codec

    ActionRegistry registry = new ActionRegistry()

    private List<DolphinServerAction> dolphinServerActions = []

    /** doesn't fail on missing commands **/
    List<Command> receive(Command command) {
        log.info "S:     received $command"
        List<Command> response = new LinkedList() // collecting parameter pattern
        dolphinServerActions.each { it.dolphinResponse = response} // todo nochmal nachdenken}

        List<CommandHandler> actions = registry[command.id]
        if (!actions) {
            log.warning "S: there is no server action registered for received command: $command, " +
                        "known commands are ${registry.actions.keySet()}"
            return response
        }
        // copying the list of actions allow an Action to unregister itself
        // avoiding ConcurrentModificationException to be thrown by the loop
        List<CommandHandler> actionsCopy = []
        actionsCopy.addAll actions
        try {
            for (CommandHandler action : actionsCopy) {
                action.handleCommand command, response
            }
        } catch (exception) {
            StackTraceUtils.deepSanitize(exception)
            log.log Level.SEVERE, "S: an error ocurred while processing $command", exception
            throw exception
        }
        return response
    }

    void register(ServerAction action){
        if (action instanceof DolphinServerAction) dolphinServerActions.add action
        action.registerIn registry
    }
}
