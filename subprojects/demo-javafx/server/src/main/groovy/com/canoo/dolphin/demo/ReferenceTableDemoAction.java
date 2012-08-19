package com.canoo.dolphin.demo;

import java.util.ArrayList;
import java.util.List;

import com.canoo.dolphin.core.ModelStore;
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand;
import com.canoo.dolphin.core.comm.GetPresentationModelCommand;
import com.canoo.dolphin.core.server.ServerAttribute;
import com.canoo.dolphin.core.server.ServerPresentationModel;
import com.canoo.dolphin.core.server.action.ServerAction;
import com.canoo.dolphin.core.server.comm.ActionRegistry;
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
		registry.register(GetPresentationModelCommand.class, new Closure(this) {
			public Object call(GetPresentationModelCommand cmd, List response) {
                String pmType = cmd.getPmId().split("-")[0];
				if (CURRENCY_REF_TABLE.equals(pmType)) {
					response.add(new CreatePresentationModelCommand(createCurrenciesPM(cmd.getPmId(), pmType)));
				}
				else if (PORTFOLIO_TYPE.equals(pmType)) {
					response.add(new CreatePresentationModelCommand(createPortfolioPM(cmd.getPmId(), pmType)));

				}
				return response;
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
