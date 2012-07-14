package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.BaseAttribute;
import com.canoo.dolphin.core.comm.InitializeAttributeCommand;
import com.canoo.dolphin.core.comm.NamedCommand;
import com.canoo.dolphin.core.server.ServerPresentationModel;
import com.canoo.dolphin.core.server.action.StoreAttributeAction;
import com.canoo.dolphin.core.server.comm.ActionRegistry;
import groovy.lang.Closure;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.canoo.dolphin.demo.DemoSearchProperties.*;

public class DemoSearchAction {

    void registerIn(ActionRegistry registry) {

        registry.register(FIRST_FILL_CMD, new Closure(this) {
            public Object call(NamedCommand cmd, List response) {
                for (int i = 0; i<10; i++){
                    String pmid = "First "+i;
                    InitializeAttributeCommand initializeAttributeCommand = new InitializeAttributeCommand();
                    initializeAttributeCommand.setPmId(pmid);
                    initializeAttributeCommand.setPropertyName(DemoSearchProperties.TEXT);
                    initializeAttributeCommand.setNewValue(pmid);

                    response.add(initializeAttributeCommand);
                }
                return response;
            }
        }  );

        registry.register(SECOND_FILL_CMD, new Closure(this) {
            public Object call(NamedCommand cmd, List response) {
                for (int i = 0; i<10; i++){
                    String pmid = "Second "+i;
                    InitializeAttributeCommand initializeAttributeCommand = new InitializeAttributeCommand();
                    initializeAttributeCommand.setPmId(pmid);
                    initializeAttributeCommand.setPropertyName(DemoSearchProperties.TEXT);
                    initializeAttributeCommand.setNewValue(pmid);

                    response.add(initializeAttributeCommand);
                }
                return response;
            }
        }  );

        registry.register(SEARCH_CMD, new Closure(this) {

            public Object call(NamedCommand cmd, List response) {

                String contactName = "";

                Map<String, ServerPresentationModel> modelStore = StoreAttributeAction.getInstance().getModelStore();
                ServerPresentationModel searchCriteria = modelStore.get(SEARCH_CRITERIA);
                List<BaseAttribute> attributes = searchCriteria.getAttributes();
                for (BaseAttribute att : attributes ) {
                    if (att.getPropertyName().equals(NAME)) {
                        contactName = att.getValue() == null ? "" : att.getValue().toString() ;
                    }
                }

                for (int i = 0; i<10; i++){
                    String pmid = contactName + " contact " + i;
                    InitializeAttributeCommand initializeAttributeCommand = new InitializeAttributeCommand();
                    initializeAttributeCommand.setPmId(pmid);
                    initializeAttributeCommand.setPropertyName(CONTACT_NAME);
                    initializeAttributeCommand.setNewValue(contactName);

                    response.add(initializeAttributeCommand);

                    InitializeAttributeCommand dateAttributeCmd = new InitializeAttributeCommand();
                    dateAttributeCmd.setPmId(pmid);
                    dateAttributeCmd.setPropertyName(CONTACT_DATE);
                    dateAttributeCmd.setNewValue(new Date(i*1000000000).toString());

                    response.add(dateAttributeCmd);
                }
                return response;
            }
        }  );
    }
}
