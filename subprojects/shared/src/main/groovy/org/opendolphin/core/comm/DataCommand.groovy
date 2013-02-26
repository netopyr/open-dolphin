package org.opendolphin.core.comm

/**
 * A command that allows the receiving side to read the data without
 * necessarily working with presentation models if that is not appropriate.
 * This is very similar to a REST response.
 */
class DataCommand extends Command {
    Map data

    DataCommand(Map data) {
        this.data = data
    }
}
