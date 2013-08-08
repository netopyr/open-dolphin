package org.opendolphin.demo

import org.opendolphin.core.comm.Command
import org.opendolphin.core.comm.GetPresentationModelCommand
import org.opendolphin.core.comm.InitializeAttributeCommand
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry
import org.opendolphin.core.server.comm.CommandHandler
import org.opendolphin.demo.data.Address
import org.opendolphin.demo.data.AddressGenerator

import static org.opendolphin.demo.LazyLoadingConstants.ATT.*
import static org.opendolphin.demo.LazyLoadingConstants.TYPE.*

public class LazyLoadingAction extends DolphinServerAction {

    List<Address> addressList

    public LazyLoadingAction(int numEntries) {
        addressList = new AddressGenerator().getAddressList(numEntries)
        // initial sorting
        Collections.sort(addressList, new Comparator<Address>() {
            @Override
            public int compare(Address address1, Address address2) {
                int result = address1.last.compareToIgnoreCase(address2.last)
                if (result == 0) {
                    result = address1.first.compareToIgnoreCase(address2.first)
                }
                if (result == 0) {
                    result = address1.city.compareToIgnoreCase(address2.city)
                }
                return result
            }
        })
    }

    @Override
    public void registerIn(ActionRegistry registry) {
        registry.register(GetPresentationModelCommand.class, new CommandHandler<GetPresentationModelCommand>() {
            public void handleCommand(GetPresentationModelCommand cmd, List<Command> response) {
                String pmId = cmd.pmId
                if (pmId == null) {
                    return
                }
                if (getServerDolphin().getAt(pmId) == null) {
                    initPresentationModel(pmId, response)
                }
            }
        })
    }

    private void initPresentationModel(String pmId, List<Command> response) {
        Address address = addressList.get(pmId.toInteger())
        response.add(createInitializeAttributeCommand(pmId, ID, pmId))
        response.add(createInitializeAttributeCommand(pmId, FIRST, address.first))
        response.add(createInitializeAttributeCommand(pmId, LAST, address.last))
        response.add(createInitializeAttributeCommand(pmId, FIRST_LAST, address.first + " " + address.last))
        response.add(createInitializeAttributeCommand(pmId, LAST_FIRST, address.last + ", " + address.first))
        response.add(createInitializeAttributeCommand(pmId, CITY, address.city))
        response.add(createInitializeAttributeCommand(pmId, PHONE, address.phone))
    }

    private InitializeAttributeCommand createInitializeAttributeCommand(String pmId, String attributeName, Object attributeValue) {
        return new InitializeAttributeCommand(pmId, attributeName, null, attributeValue, LAZY)
    }
}
