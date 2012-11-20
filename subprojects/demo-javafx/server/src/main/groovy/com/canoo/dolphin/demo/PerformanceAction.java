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

package com.canoo.dolphin.demo;

import com.canoo.dolphin.core.Attribute;
import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.PresentationModel;
import com.canoo.dolphin.core.comm.Command;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;
import com.canoo.dolphin.core.comm.DataCommand;
import com.canoo.dolphin.core.server.ServerDolphin;
import com.canoo.dolphin.core.server.ServerPresentationModel;
import com.canoo.dolphin.core.server.action.DolphinServerAction;
import com.canoo.dolphin.core.server.action.ServerAction;
import com.canoo.dolphin.core.server.comm.ActionRegistry;
import com.canoo.dolphin.core.server.comm.CommandHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.canoo.dolphin.demo.DemoSearchProperties.*;



public class PerformanceAction extends DolphinServerAction {
    static long id = 0;
    public void registerIn(final ActionRegistry registry) {
        registry.register("sync", new CommandHandler() {
            public void handleCommand(Command command, List response) {
                // do nothing
                // this is only for synchronizing the timer after "clear"
            }
        });

        registry.register("stressTest", new CommandHandler() {
            public void handleCommand(Command command, List response) {
                PresentationModel pm = getServerDolphin().findPresentationModelById("input");
                if (pm == null) {
                    throw new IllegalStateException("No input criteria known on the server!");
                }
                Attribute countAtt = pm.findAttributeByPropertyName("count");
                Object countValue = (countAtt == null) ? null : countAtt.getValue();
                int count = (countValue == null) ? 1 : Integer.parseInt(countValue.toString());

                Attribute attCountAtt = pm.findAttributeByPropertyName("attCount");
                Object attCountValue = (attCountAtt == null) ? null : attCountAtt.getValue();
                int attCount = (attCountValue == null) ? 1 : Integer.parseInt(attCountValue.toString());

                for (int pmCount = 0; pmCount < count; pmCount++) {
                    Map<String, Object> attributeMap = new HashMap<String, Object>();
                    for (int attI = 0; attI < attCount; attI++) {
                        attributeMap.put("att"+Long.toString(id++), "val"+Long.toString(id++));
                    }
                    ServerPresentationModel model = getServerDolphin().presentationModel(attributeMap, Long.toString(id++), "all");
                    response.add(CreatePresentationModelCommand.makeFrom(model));
                }
            }
        });
    }
}
