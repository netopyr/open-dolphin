package com.canoo.dolphin.demo

import java.util.logging.*
import com.canoo.dolphin.core.client.comm.InMemoryClientConnector

/** Keep logging details in one place **/

class LogConfig {

    private static final Logger ROOT_LOGGER = LogManager.logManager.getLogger('')

    static {
        ROOT_LOGGER.handlers.grep(ConsoleHandler).each { it.formatter = new ShortFormatter() }
    }

    static noLogs() {
        ROOT_LOGGER.level = Level.OFF
    }

    static logCommunication() {
        InMemoryClientConnector.log.level = Level.INFO
    }

}

class ShortFormatter extends SimpleFormatter {
    synchronized String format(LogRecord record) {
        "[$record.level] $record.message\n"
    }
}