package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel

// todo dk: review this design.

class CreatePresentationModelCommand extends Command {
    String pmId
    String pmType
    List<Map<String, Object>> attributes = [] // todo dk: I wouldn't want to have collective command properties

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
