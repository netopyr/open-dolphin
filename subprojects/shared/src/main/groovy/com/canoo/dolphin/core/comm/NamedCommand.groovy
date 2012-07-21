package com.canoo.dolphin.core.comm

import groovy.transform.TupleConstructor

/**
 * A command where the id can be set from the outside for general purposes.
 */
@TupleConstructor
class NamedCommand extends Command {
    String id
}
