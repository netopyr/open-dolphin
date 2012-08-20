package com.canoo.dolphin.core.comm

public interface Codec {

    String encode(List<Command> commands)

    List<Command> decode(String transmitted)

}