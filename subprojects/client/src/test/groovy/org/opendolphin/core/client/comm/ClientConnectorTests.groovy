/*
 * Copyright 2012-2013 Canoo Engineering AG.
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

package org.opendolphin.core.client.comm

import groovy.util.logging.Log
import org.opendolphin.core.Attribute
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.core.comm.*

import java.beans.PropertyChangeEvent
import java.util.concurrent.CountDownLatch

class ClientConnectorTests extends GroovyTestCase {

    TestClientConnector clientConnector
    ClientDolphin dolphin

    protected void setUp() {
        dolphin = new ClientDolphin()
        clientConnector = new TestClientConnector(dolphin)
        clientConnector.uiThreadHandler = { it() } as UiThreadHandler
        dolphin.clientConnector = clientConnector
        dolphin.clientModelStore = new ClientModelStore(dolphin)
    }

    void testHandleSimpleCreatePresentationModelCommand() {
        final myPmId = "myPmId"
        assert null == dolphin.findPresentationModelById(myPmId)
        CreatePresentationModelCommand command = new CreatePresentationModelCommand()
        command.pmId = myPmId
        def result = clientConnector.handle(command)
        assert myPmId == result.id
        assert dolphin.findPresentationModelById(myPmId)
    }

    void testDefaultOnExceptionHandler() {
        def msg = shouldFail(RuntimeException) {
            clientConnector.onException(new RuntimeException("test exception"))
        }
        println "..."
        println msg
    }

    void testPropertyChange_DirtyPropertyIgnored() {
        clientConnector.propertyChange(new PropertyChangeEvent("dummy", Attribute.DIRTY_PROPERTY, null, null))
        assert 0 == clientConnector.getTransmitCount(2)
    }

    void testValueChange_OldAndNewValueSame() {
        clientConnector.propertyChange(new PropertyChangeEvent("dummy", Attribute.VALUE, 'sameValue', 'sameValue'))
        assert 0 == clientConnector.getTransmitCount(2)
    }

    void testValueChange() {
        ClientAttribute attribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(attribute)
        clientConnector.propertyChange(new PropertyChangeEvent(attribute, Attribute.VALUE, attribute.value, 'newValue'))
        assert 2 == clientConnector.transmitCount
        assert attribute.value == 'newValue'
        assert 2 == clientConnector.transmittedCommands.size()
        assert clientConnector.transmittedCommands.any { it instanceof ValueChangedCommand }
    }

    void testBaseValueChange_OldAndNewValueSame() {
        clientConnector.propertyChange(new PropertyChangeEvent("dummy", Attribute.BASE_VALUE, 'sameValue', 'sameValue'))
        assert 0 == clientConnector.getTransmitCount(2)
    }

    void testBaseValueChange() {
        ClientAttribute attribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
        attribute.value = 'newValue'
        dolphin.clientModelStore.registerAttribute(attribute)
        clientConnector.propertyChange(new PropertyChangeEvent(attribute, Attribute.BASE_VALUE, '', 'newValue'))
        assert 2 == clientConnector.transmitCount
        assert attribute.baseValue == 'newValue'
        assert 2 == clientConnector.transmittedCommands.size()
        assert clientConnector.transmittedCommands.any { it instanceof InitialValueChangedCommand }
    }

    void testMetaDataChange() {
        ClientAttribute attribute = new ExtendedAttribute('attr', 'initialValue', 'qualifier')
        attribute.value = 'newValue'
        dolphin.clientModelStore.registerAttribute(attribute)
        clientConnector.propertyChange(new PropertyChangeEvent(attribute, 'additionalParam', null, 'newTag'))
        sleep(100)
        assert 1 == clientConnector.getTransmitCount(1)
        assert 1 == clientConnector.transmittedCommands.size()
        assert ChangeAttributeMetadataCommand == clientConnector.transmittedCommands[0].class
        assert 'newTag' == attribute.additionalParam
    }

    void testMetaDataChange_UnregisteredAttribute() {
        ClientAttribute attribute = new ExtendedAttribute('attr', 'initialValue', 'qualifier')
        attribute.additionalParam = 'oldValue'
        clientConnector.propertyChange(new PropertyChangeEvent(attribute, 'additionalParam', null, 'newTag'))
        sleep(100)
        assert 1 == clientConnector.getTransmitCount(1)
        assert 1 == clientConnector.transmittedCommands.size()
        assert ChangeAttributeMetadataCommand == clientConnector.transmittedCommands[0].class
        assert 'oldValue' == attribute.additionalParam
    }

    void testHandle_PresentationModelReseted() {
        dolphin.presentationModel('p1')
        assert clientConnector.handle(new PresentationModelResetedCommand(pmId: 'p1'))
    }

    void testHandle_PresentationModelReseted_PmNotExists() {
        assert !clientConnector.handle(new PresentationModelResetedCommand(pmId: 'notExist'))
    }

    void testSavePresentationModel() {
        dolphin.presentationModel('p1')
        clientConnector.clientModelStore.save('p1')
        sleep(100)
        assert 2 == clientConnector.getTransmitCount(1)
        assert 2 == clientConnector.transmittedCommands.size()
        assert CreatePresentationModelCommand == clientConnector.transmittedCommands[0].class
        assert SavePresentationModelCommand == clientConnector.transmittedCommands[1].class
    }

    void testHandle_InitializeAttribute() {
        def syncedAttribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(syncedAttribute)
        clientConnector.handle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', 'newValue'))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('newProp')
        assert 'newValue' == dolphin.getAt('p1').getAt('newProp').value
        assert 'newValue' == syncedAttribute.value

    }

    void testHandle_InitializeAttribute_NewValueNotSet() {
        def syncedAttribute = new ClientAttribute('attr', 'initialValue', 'qualifier')
        dolphin.clientModelStore.registerAttribute(syncedAttribute)
        clientConnector.handle(new InitializeAttributeCommand('p1', 'newProp', 'qualifier', null))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('newProp')
        assert 'initialValue' == dolphin.getAt('p1').getAt('newProp').value
        assert 'initialValue' == syncedAttribute.value

    }

    void testHandle_SwitchPresentationModel_PmNotExists() {
        assert !clientConnector.handle(new SwitchPresentationModelCommand(sourcePmId: 'p1', pmId: 'p2'))
        dolphin.presentationModel('p2')
        assert !clientConnector.handle(new SwitchPresentationModelCommand(sourcePmId: 'p1', pmId: 'p2'))
    }

    void testHandle_SwitchPresentationModel() {
        ClientPresentationModel model_one = dolphin.presentationModel('p1')
        model_one.addAttribute(new ClientAttribute('attr', 'one'))
        ClientPresentationModel model_two = dolphin.presentationModel('p2')
        model_two.addAttribute(new ClientAttribute('attr', 'two'))
        assert clientConnector.handle(new SwitchPresentationModelCommand(sourcePmId: 'p1', pmId: 'p2'))
        assert 'one' == dolphin.getAt('p2').getAt('attr').value
    }

    void testHandle_InitialValueChanged_AttrNotExists() {
        def attribute = new ClientAttribute('attr', 'initialValue')
        assert !clientConnector.handle(new InitialValueChangedCommand(attributeId: attribute.id))
    }

    void testHandle_InitialValueChanged() {
        def attribute = new ClientAttribute('attr', 'initialValue')
        attribute.value = 'newValue'
        dolphin.clientModelStore.registerAttribute(attribute)
        clientConnector.handle(new InitialValueChangedCommand(attributeId: attribute.id))
        assert 'newValue' == attribute.baseValue
    }

    void testHandle_ValueChanged_AttrNotExists() {
        assert !clientConnector.handle(new ValueChangedCommand(attributeId: 0, oldValue: 'oldValue', newValue: 'newValue'))
    }

    void testHandle_ValueChanged() {
        def attribute = new ClientAttribute('attr', 'initialValue')
        dolphin.clientModelStore.registerAttribute(attribute)
        assert !clientConnector.handle(new ValueChangedCommand(attributeId: attribute.id, oldValue: 'oldValue', newValue: 'newValue'))
        assert 'newValue' == attribute.value
    }

    void testHandle_CreatePresentationModel() {
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('attr')
        assert 'initialValue' == dolphin.getAt('p1').getAt('attr').value
        assert 'qualifier' == dolphin.getAt('p1').getAt('attr').qualifier
        sleep(100)
        assert 1 == clientConnector.getTransmitCount(1)
        assert CreatePresentationModelCommand == clientConnector.transmittedCommands[0].class
    }

    void testHandle_CreatePresentationModel_ClientSideOnly() {
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', clientSideOnly: true, attributes: [[propertyName: 'attr', value: 'initialValue', qualifier: 'qualifier']]))
        assert dolphin.getAt('p1')
        assert dolphin.getAt('p1').getAt('attr')
        assert 'initialValue' == dolphin.getAt('p1').getAt('attr').value
        assert 'qualifier' == dolphin.getAt('p1').getAt('attr').qualifier
        sleep(100)
        assert 0 == clientConnector.getTransmitCount(2)
    }

    void testHandle_CreatePresentationModel_ClientSideOnly_MergeAttributesToExistingModel() {
        dolphin.presentationModel('p1')
        def attribute = new ClientAttribute('attr')
        dolphin.getAt('p1').addAttribute(attribute)
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type', clientSideOnly: true,
                attributes: [
                        [propertyName: 'attr', id: 20],
                        [propertyName: 'attr2', value: 'initialValue2', qualifier: 'qualifier']
                ]))
        assert 20 == dolphin.getAt('p1').getAt('attr').id
        assert 'initialValue2' == dolphin.getAt('p1').getAt('attr2').value
        assert 'qualifier' == dolphin.getAt('p1').getAt('attr2').qualifier
    }
    void testHandle_CreatePresentationModel_MergeAttributesToExistingModel() {
        dolphin.presentationModel('p1')
        def attribute = new ClientAttribute('attr')
        dolphin.getAt('p1').addAttribute(attribute)
        assert clientConnector.handle(new CreatePresentationModelCommand(pmId: 'p1', pmType: 'type',
                attributes: [
                        [propertyName: 'attr', id: 20],
                        [propertyName: 'attr2', value: 'initialValue2', qualifier: 'qualifier']
                ]))
        assert 20 == dolphin.getAt('p1').getAt('attr').id
        assert 'initialValue2' == dolphin.getAt('p1').getAt('attr2').value
        assert 'qualifier' == dolphin.getAt('p1').getAt('attr2').qualifier
    }

    void testHandle_DeletePresentationModel() {
        ClientPresentationModel p1 = dolphin.presentationModel('p1')
        p1.clientSideOnly = true
        ClientPresentationModel p2 = dolphin.presentationModel('p2')
        clientConnector.handle(new DeletePresentationModelCommand(pmId: null))
        def model = new ClientPresentationModel('p3', [])
        clientConnector.handle(new DeletePresentationModelCommand(pmId: model.id))
        clientConnector.handle(new DeletePresentationModelCommand(pmId: p1.id))
        clientConnector.handle(new DeletePresentationModelCommand(pmId: p2.id))
        assert !dolphin.getAt(p1.id)
        assert !dolphin.getAt(p2.id)
        sleep(100)
        assert 3 == clientConnector.getTransmitCount()
        assert 1 == clientConnector.transmittedCommands.findAll { it instanceof DeletedPresentationModelNotification }.size()
    }

    void testHandle_DataCommand() {
        def data = [k: 'v']
        assert data == clientConnector.handle(new DataCommand(data))
    }
}

@Log
class TestClientConnector extends ClientConnector {
    CountDownLatch latch = new CountDownLatch(2)
    List<Command> transmittedCommands = []

    TestClientConnector(ClientDolphin clientDolphin) {
        super(clientDolphin)
    }

    int getPoolSize() { 1 }

    int getTransmitCount(int countDownLatch = 0) {
        countDownLatch.times { latch.countDown() }
        latch.await()
        transmittedCommands.size()
    }

    List<Command> transmit(Command command) {
        transmittedCommands << command
        latch.countDown()
        return construct(command)
    }

    List construct(ChangeAttributeMetadataCommand command) {
        [new AttributeMetadataChangedCommand(attributeId: command.attributeId, metadataName: command.metadataName, value: command.value)]
    }

    List construct(Command command) {
        []
    }

    List construct(ResetPresentationModelCommand command) {
        [new PresentationModelResetedCommand(pmId: command.pmId)]
    }

    List construct(SavePresentationModelCommand command) {
        [new SavedPresentationModelNotification(pmId: command.pmId)]
    }
}

class ExtendedAttribute extends ClientAttribute {
    String additionalParam

    ExtendedAttribute(String propertyName, Object initialValue, String qualifier) {
        super(propertyName, initialValue, qualifier)
    }
}
