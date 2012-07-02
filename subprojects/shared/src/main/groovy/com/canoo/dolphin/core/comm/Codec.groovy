package com.canoo.dolphin.core.comm

import org.codehaus.groovy.tools.shell.Command

public interface Codec {

    String encode(List<Command> commands) // yes, I know this should be ? (extends|super) Command or so...

    List<Command> decode(String transmitted)

}