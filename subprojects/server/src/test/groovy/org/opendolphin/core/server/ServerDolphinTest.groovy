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

package org.opendolphin.core.server

public class ServerDolphinTest extends GroovyTestCase{
    ServerDolphin dolphin

    @Override
    protected void setUp() throws Exception {
        dolphin = new ServerDolphin()
    }

    // todo dk: creating a SPM adds the respective commands to the response

    void testListPresentationModels() {
        assert dolphin.listPresentationModelIds().empty
        assert dolphin.listPresentationModels().empty

        def pm1 = new ServerPresentationModel("first", [])
        dolphin.modelStore.add pm1

        assert ['first'].toSet() == dolphin.listPresentationModelIds()
        assert [pm1]             == dolphin.listPresentationModels().toList()

        def pm2 = new ServerPresentationModel("second", [])
        dolphin.modelStore.add pm2

        assert 2 == dolphin.listPresentationModelIds().size()
        assert 2 == dolphin.listPresentationModels().size()

        for (id in dolphin.listPresentationModelIds()){
            assert dolphin.findPresentationModelById(id) in dolphin.listPresentationModels()
        }
    }
}
