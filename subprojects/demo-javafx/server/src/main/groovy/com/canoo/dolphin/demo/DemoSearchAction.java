package com.canoo.dolphin.demo;


import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.comm.InitializeAttributeCommand;
import com.canoo.dolphin.core.comm.NamedCommand;
import com.canoo.dolphin.core.server.action.ServerAction;
import com.canoo.dolphin.core.server.comm.ActionRegistry;
import groovy.lang.Closure;

import java.util.Date;
import java.util.List;

import static com.canoo.dolphin.demo.DemoSearchProperties.*;

public class DemoSearchAction implements ServerAction {
    protected final ModelStore modelStore;

    public DemoSearchAction(ModelStore modelStore) {
        this.modelStore = modelStore;
    }

    public void registerIn(final ActionRegistry registry) {

        registry.register(FIRST_FILL_CMD, new Closure(this) {
            public Object call(NamedCommand cmd, List response) {
                for (int i = 0; i<10; i++){
                    String pmid = "First "+i;
                    response.add(new InitializeAttributeCommand(pmid, TEXT, null, pmid));
                }
                return response;
            }
        }  );

        registry.register(SECOND_FILL_CMD, new Closure(this) {
            public Object call(NamedCommand cmd, List response) {
                for (int i = 0; i<10; i++){
                    String pmid = "Second "+i;
                    response.add(new InitializeAttributeCommand(pmid, TEXT, null, pmid));
                }
                return response;
            }
        }  );

        registry.register(SEARCH_CMD, new Closure(this) {

            public Object call(NamedCommand cmd, List response) {

                PresentationModel searchCriteria = modelStore.findPresentationModelById(SEARCH_CRITERIA);
                if (searchCriteria == null) {
                    throw new IllegalStateException("No search criteria known on the server!");
                }
                Attribute attribute = searchCriteria.findAttributeByPropertyName(NAME);
                Object value = (attribute == null) ? null : attribute.getValue();
                String contactName = (value == null) ? "" : value.toString();

                for (int i = 0; i<10; i++){
                    String pmid = contactName + " contact " + i;
                    response.add(new InitializeAttributeCommand(pmid,CONTACT_NAME, null, contactName));
                    response.add(new InitializeAttributeCommand(pmid, CONTACT_DATE, null, new Date(i*1000000000).toString()));
                }
                return response;
            }
        }  );
    }
}
