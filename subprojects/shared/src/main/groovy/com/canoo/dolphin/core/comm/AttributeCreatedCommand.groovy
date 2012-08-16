package com.canoo.dolphin.core.comm

// todo dk: rename to AttributeCreatedNotification

class AttributeCreatedCommand extends Command {

    String  pmId
    long    attributeId
    String  propertyName
    def     newValue
    String  qualifier

    String toString() { super.toString() + " attr:$attributeId, pm:$pmId, property:$propertyName value:$newValue qualifier:$qualifier"}
}
