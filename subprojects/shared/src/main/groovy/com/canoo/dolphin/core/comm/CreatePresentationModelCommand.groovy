/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.core.comm

import com.canoo.dolphin.core.PresentationModel

class CreatePresentationModelCommand extends Command {
    String pmId
    String pmType
    List<Map<String, Object>> attributes = []

    // note: we always need a paramless ctor for the codec

    /** @deprecated use ServerFacade convenience methods (it is ok to use it from the client atm)*/
    static CreatePresentationModelCommand makeFrom(PresentationModel model) {
        def result = new CreatePresentationModelCommand()
        result.pmId = model.id
        result.pmType = model.presentationModelType
        model.attributes.each { attr ->
            result.attributes << [
                    propertyName:   attr.propertyName,
                    id:             attr.id,
                    qualifier:      attr.qualifier,
                    value:          attr.value,
                    tag:            attr.tag.name()
            ]
        }
        return result
    }

    String toString() {super.toString()+ " pmId $pmId pmType $pmType attributes $attributes"}
}
