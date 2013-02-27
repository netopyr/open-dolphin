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

package org.opendolphin


import org.opendolphin.core.client.comm.ClientConnector

/** Keep logging details in one place **/
// todo dk: provide a consistent counterpart for the server
class LogConfig {

    private static final Logger ROOT_LOGGER = Logger.getLogger("")

    static {
        ROOT_LOGGER.handlers.grep(ConsoleHandler).each { it.formatter = new ShortFormatter() }
    }

    static noLogs() {
        logOnLevel(Level.OFF)
    }

    static logCommunication() {
        logOnLevel(Level.INFO)
    }

    static logOnLevel(Level level) {
        ROOT_LOGGER.level = level
        ClientConnector.log.level = level
    }
}

import java.util.logging.*

class ShortFormatter extends SimpleFormatter {
    synchronized String format(LogRecord record) {
        "[$record.level] $record.message\n"
    }
}