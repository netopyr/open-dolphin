package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel

class CreatePresentationModelCommand extends Command{
    String pmId
    String pmType
    List<Map<String, Object>> attributes = []

    CreatePresentationModelCommand(PresentationModel model) {
        pmId = model.id
        pmType = model.presentationModelType
        model.attributes.each { attr ->
            attributes << [
                    propertyName: attr.propertyName,
                    id: attr.id,
                    dataId: attr.dataId,
                    value: attr.value
            ]
        }
    }

    String toString() {super.toString()+ " pmId $pmId pmType $pmType attributes $attributes"}
}
