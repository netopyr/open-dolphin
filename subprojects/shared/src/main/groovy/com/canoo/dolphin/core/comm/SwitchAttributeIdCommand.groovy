package com.canoo.dolphin.core.comm

//todo dk: this should be removed
class SwitchAttributeIdCommand extends Command {

    String pmId
    String propertyName
    long newId

    String toString() { super.toString() + " in $pmId: $propertyName -> $newId"}
}
