package com.canoo.dolphin.core.comm

class SwitchAttributeIdCommand extends Command {

    String pmId
    String propertyName
    long newId

    String toString() { super.toString() + " in $pmId: $propertyName -> $newId"}
}
