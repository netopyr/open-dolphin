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
        parent.presentationModelType = 'parent'
        PresentationModel child1 = new BasePresentationModel("1", [])
        PresentationModel child2 = new BasePresentationModel("2", [])
        PresentationModel child3 = new BasePresentationModel("3", [])

        StoreListener storeListener = new StoreListener()
        StoreListener parentStoreListener = new StoreListener()
        LinkListener linkListener = new LinkListener()
        LinkListener selfReferenceLinkListener = new LinkListener()

        ModelStore modelStore = new ModelStore()
        modelStore.addModelStoreListener(storeListener)
        modelStore.addModelStoreListener('parent', parentStoreListener)
        modelStore.addModelStoreLinkListener(linkListener)
        modelStore.addModelStoreLinkListener(PmLinkTypes.SELF_REFERENCE.name(), selfReferenceLinkListener)

        modelStore.add(parent)

        assert storeListener.event
        assert storeListener.event.presentationModel == parent
        assert storeListener.event.type == ModelStoreEvent.Type.ADDED
        assert parentStoreListener.event
        assert parentStoreListener.event.presentationModel == parent
        assert parentStoreListener.event.type == ModelStoreEvent.Type.ADDED

        storeListener.event = null
        parentStoreListener.event = null

        modelStore.add(child1)

        assert storeListener.event
        assert storeListener.event.presentationModel == child1
        assert storeListener.event.type == ModelStoreEvent.Type.ADDED
        assert !parentStoreListener.event

        modelStore.add(child2)
        // child3 is not added to the store

        // no links should exist at this point
        assert !modelStore.linkExists(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(parent, child3, PmLinkTypes.PARENT_CHILD.name())
        assert !linkListener.event
        assert !selfReferenceLinkListener.event

        assert modelStore.link(parent, child1, PmLinkTypes.PARENT_CHILD.name())

        assert linkListener.event
        assert !selfReferenceLinkListener.event
        assert linkListener.event.type == ModelStoreLinkEvent.Type.ADDED
        assert linkListener.event.start == parent
        assert linkListener.event.end == child1
        assert linkListener.event.linkType == PmLinkTypes.PARENT_CHILD.name()

        assert modelStore.link(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.link(parent, child3, PmLinkTypes.PARENT_CHILD.name())
        assert modelStore.link(parent, parent, PmLinkTypes.SELF_REFERENCE.name())

        assert linkListener.event
        assert selfReferenceLinkListener.event
        assert linkListener.event.type == ModelStoreLinkEvent.Type.ADDED
        assert linkListener.event.start == parent
        assert linkListener.event.end == parent
        assert linkListener.event.linkType == PmLinkTypes.SELF_REFERENCE.name()
        assert selfReferenceLinkListener.event.type == ModelStoreLinkEvent.Type.ADDED
        assert selfReferenceLinkListener.event.start == parent
        assert selfReferenceLinkListener.event.end == parent
        assert selfReferenceLinkListener.event.linkType == PmLinkTypes.SELF_REFERENCE.name()

        assert modelStore.link(child1, child2, PmLinkTypes.SIBLING.name())

        assert modelStore.linkExists(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        assert modelStore.linkExists(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(parent, child3, PmLinkTypes.PARENT_CHILD.name())
        assert modelStore.linkExists(parent, parent, PmLinkTypes.SELF_REFERENCE.name())
        assert modelStore.linkExists(child1, child2, PmLinkTypes.SIBLING.name())
        assert !modelStore.linkExists(child1, child2, "<unknown>")

        Link link1 = modelStore.findLink(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        Link link2 = modelStore.findLink(parent, child2, PmLinkTypes.PARENT_CHILD.name())
        Link link3 = modelStore.findLink(parent, child3, PmLinkTypes.PARENT_CHILD.name())
        Link link4 = modelStore.findLink(parent, parent, PmLinkTypes.SELF_REFERENCE.name())
        Link link5 = modelStore.findLink(child1, child2, PmLinkTypes.SIBLING.name())

        assert link1
        assert link2
        assert !link3
        assert link4
        assert link5

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

        List outgoing = modelStore.findAllLinksByModel(child1, Link.Direction.OUTGOING)
        assert 1 == outgoing.size()
        assert child1 == outgoing[0].start
        assert child2 == outgoing[0].end

        List incoming = modelStore.findAllLinksByModel(child1, Link.Direction.INCOMING)
        assert 1 == incoming.size()
        assert parent == incoming[0].start
        assert child1 == incoming[0].end

        List all = modelStore.findAllLinksByModel(child1)
        assert 2 == all.size()

        linkListener.event = null
        selfReferenceLinkListener.event = null
        modelStore.unlink(link1)
        assert !modelStore.linkExists(parent, child1, PmLinkTypes.PARENT_CHILD.name())
        assert !modelStore.linkExists(link1)
        assert linkListener.event.type == ModelStoreLinkEvent.Type.REMOVED
        assert linkListener.event.start == link1.start
        assert linkListener.event.end == link1.end
        assert linkListener.event.linkType == link1.type
        assert !selfReferenceLinkListener.event

        modelStore.unlink(link2)
        modelStore.unlink(link3)
        modelStore.unlink(link4)
        modelStore.unlink(link5)

        assert !modelStore.findAllLinksByModel(parent)
        assert !modelStore.findAllLinksByModelAndType(parent, PmLinkTypes.SELF_REFERENCE.name())
        assert !modelStore.findAllLinksByModelAndType(parent, PmLinkTypes.PARENT_CHILD.name())
    }
}

enum PmLinkTypes {
    PARENT_CHILD, SELF_REFERENCE, SIBLING
}

class StoreListener implements ModelStoreListener {
    ModelStoreEvent event

    @Override
    void modelStoreChanged(ModelStoreEvent event) {
        this.event = event
    }
}

class LinkListener implements ModelStoreLinkListener {
    ModelStoreLinkEvent event

    @Override
    void modelStoreLinkChanged(ModelStoreLinkEvent event) {
        this.event = event
    }
}