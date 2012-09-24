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

package com.canoo.dolphin.core

class ModelStoreTest extends GroovyTestCase {
    void testLinkSmokeTests() {
        PresentationModel parent = new BasePresentationModel("0", [])
        PresentationModel child1 = new BasePresentationModel("1", [])
        PresentationModel child2 = new BasePresentationModel("2", [])
        PresentationModel child3 = new BasePresentationModel("3", [])

        ModelStore modelStore = new ModelStore()
        modelStore.add(parent)
        modelStore.add(child1)
        modelStore.add(child2)
        // child3 is not added to the store

        // no links should exist at this point
        assert !modelStore.linkExists(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(parent, child3, PmLinkTypes.PARENT_CHILD.name())

        Link link1 = modelStore.link(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        Link link2 = modelStore.link(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        Link link3 = modelStore.link(parent, child3, PmLinkTypes.PARENT_CHILD.name())
        Link link4 = modelStore.link(parent, parent, PmLinkTypes.SELF_REFERENCE.name())

        assert link1
        assert link2
        assert !link3  // this link should not exists as child3 is not found in the store
        assert link4

        assert modelStore.linkExists(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        assert modelStore.linkExists(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(parent, child3, PmLinkTypes.PARENT_CHILD.name())

        List children = modelStore.findAllLinksByModelAndType(parent, PmLinkTypes.PARENT_CHILD.name())
        assert 2 == children.size()
        assert link1 in children
        assert link2 in children
        assert !(link3 in children)

        List selfReferences = modelStore.findAllLinksByModelAndType(parent, PmLinkTypes.SELF_REFERENCE.name())
        assert 1 == selfReferences.size()
        assert link4 in selfReferences

        List links = modelStore.findAllLinksByModel(parent)
        assert 3 == links.size()
        assert link1 in links
        assert link2 in links
        assert link4 in links

        modelStore.unlink(link1)
        assert !modelStore.linkExists(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(link1)

        modelStore.unlink(link2)
        modelStore.unlink(link3)
        modelStore.unlink(link4)

        assert !modelStore.findAllLinksByModel(parent)
        assert !modelStore.findAllLinksByModelAndType(parent, PmLinkTypes.SELF_REFERENCE.name())
        assert !modelStore.findAllLinksByModelAndType(parent, PmLinkTypes.PARENT_CHILD.name())
    }
}

enum PmLinkTypes {
    PARENT_CHILD, SELF_REFERENCE
}