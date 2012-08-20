package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel

class CreatePresentationModelCommand extends Command {
    String pmId
    String pmType
    List<Map<String, Object>> attributes = []

    // note: we always need a paramless ctor for the codec

    static CreatePresentationModelCommand makeFrom(PresentationModel model) {
        def result = new CreatePresentationModelCommand()
        result.pmId = model.id
        result.pmType = model.presentationModelType
        model.attributes.each { attr ->
            result.attributes << [
                    propertyName: attr.propertyName,
                    id: attr.id,
                    qualifier: attr.qualifier,
                    value: attr.value
            ]
        }
        return result
    }

    String toString() {super.toString()+ " pmId $pmId pmType $pmType attributes $attributes"}
}
