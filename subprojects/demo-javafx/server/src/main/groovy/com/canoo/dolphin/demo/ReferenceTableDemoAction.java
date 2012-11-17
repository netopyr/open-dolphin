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

import java.util.ArrayList;
import java.util.List;

import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.comm.Command;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;
import com.canoo.dolphin.core.comm.GetPresentationModelCommand;
import com.canoo.dolphin.core.server.ServerAttribute;
import com.canoo.dolphin.core.server.ServerPresentationModel;
import com.canoo.dolphin.core.server.action.ServerAction;
import com.canoo.dolphin.core.server.comm.ActionRegistry;
import com.canoo.dolphin.core.server.comm.CommandHandler;
import groovy.lang.Closure;

import static com.canoo.dolphin.demo.ReferenceTableDemoProperties.CURRENCY;
import static com.canoo.dolphin.demo.ReferenceTableDemoProperties.CURRENCY_REF_TABLE;
import static com.canoo.dolphin.demo.ReferenceTableDemoProperties.PORTFOLIO_TYPE;

public class ReferenceTableDemoAction implements ServerAction {
	protected final ModelStore modelStore;

	public ReferenceTableDemoAction(ModelStore modelStore) {
		this.modelStore = modelStore;
	}

	public void registerIn(final ActionRegistry registry) {
		registry.register(GetPresentationModelCommand.class, new CommandHandler<GetPresentationModelCommand>() {
            @Override
            public void handleCommand(GetPresentationModelCommand cmd, List response) {
                String pmType = cmd.getPmId().split("-")[0];
				if (CURRENCY_REF_TABLE.equals(pmType)) {
					response.add(CreatePresentationModelCommand.makeFrom(createCurrenciesPM(cmd.getPmId(), pmType)));
				}
				else if (PORTFOLIO_TYPE.equals(pmType)) {
					response.add(CreatePresentationModelCommand.makeFrom(createPortfolioPM(cmd.getPmId(), pmType)));

				}
			}
		});
	}

	private static ServerPresentationModel createCurrenciesPM(String pmId, String type) {
		List<ServerAttribute> attributes = new ArrayList<ServerAttribute>();
		attributes.add(new ServerAttribute("110", "CHF"));
		attributes.add(new ServerAttribute("220", "USD"));
		attributes.add(new ServerAttribute("330", "EUR"));
		ServerPresentationModel model = new ServerPresentationModel(pmId, attributes);
		model.setPresentationModelType(type);

		return model;
	}

	private static ServerPresentationModel createPortfolioPM(String pmId, String type) {
		List<ServerAttribute> attributes = new ArrayList<ServerAttribute>();
		attributes.add(new ServerAttribute(CURRENCY, ""));
		ServerPresentationModel portfolio = new ServerPresentationModel(pmId, attributes);
		portfolio.setPresentationModelType(type);

		return portfolio;
	}

}
