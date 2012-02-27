package com.canoo.dolphin.core.comm

class Command {

    String commandId
    String userId

    String toString() { "user:$userId command:$commandId"}
}
