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

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.comm.PresentationModelLinkAddedCommand

def config = new JavaFxInMemoryConfig()
config.serverDolphin.action('loadPms') { cmd, response ->
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('parent0', 'parent',
                    column0: 'P00', column1: 'P01')
    )
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('parent1', 'parent',
                    column0: 'P10', column1: 'P11')
    )
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('parent2', 'parent',
                    column0: 'P02', column1: 'P22')
    )
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('child00',
                    column0: 'C000', column1: 'C001', column2: 'C002')
    )
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('child01',
                    column0: 'C010', column1: 'C011', column2: 'C012')
    )
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('child10',
                    column0: 'C100', column1: 'C101', column2: 'C102')
    )
    response << CreatePresentationModelCommand.makeFrom(
            config.serverDolphin.presentationModel('child11',
                    column0: 'C110', column1: 'C111', column2: 'C112')
    )
}
config.serverDolphin.action('linkPms') { cmd, response ->
    response << new PresentationModelLinkAddedCommand(startId: 'parent0', endId: 'child00', type: 'PARENT_CHILD')
    response << new PresentationModelLinkAddedCommand(startId: 'parent0', endId: 'child01', type: 'PARENT_CHILD')
    response << new PresentationModelLinkAddedCommand(startId: 'parent1', endId: 'child10', type: 'PARENT_CHILD')
    response << new PresentationModelLinkAddedCommand(startId: 'parent1', endId: 'child11', type: 'PARENT_CHILD')
}

new PresentationModelLinksView().show(config.clientDolphin)