package com.canoo.dolphin.core.comm

class AttributeCreatedCommand extends Command {

    String  pmId
    long    attributeId
    String  propertyName
    def     newValue
    String  dataId

    String toString() { super.toString() + " attr:$attributeId, pm:$pmId, property:$propertyName value:$newValue dataId:$dataId"}
}
