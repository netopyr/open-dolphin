package com.canoo.dolphin.core.comm

class ChangeAttributeMetadataCommand extends Command {
    long    attributeId
    String  metadataName
    Object  value

    String toString() { super.toString() + " attr:$attributeId, metadataName:$metadataName value:$value"}
}
