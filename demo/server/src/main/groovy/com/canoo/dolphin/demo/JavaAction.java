package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.comm.InitializeAttributeCommand;
import com.canoo.dolphin.core.comm.NamedCommand;
import com.canoo.dolphin.core.server.comm.ActionRegistry;
import groovy.lang.Closure;

import java.util.List;

public class JavaAction {

    void registerIn(ActionRegistry registry) {

        registry.register("javaAction", new Closure(this) {

            public Object call(NamedCommand cmd, List response) {

                InitializeAttributeCommand initializeAttributeCommand = new InitializeAttributeCommand();
                initializeAttributeCommand.setPmId("JAVA");
                initializeAttributeCommand.setPropertyName("purpose");
                initializeAttributeCommand.setNewValue("works without JavaFX");

                response.add(initializeAttributeCommand);
                return response;
            }
        }  );
    }
}
