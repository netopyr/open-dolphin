package org.opendolphin.demo.crud

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.opendolphin.core.client.ClientPresentationModel

import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED

class SampleModel {
    ObservableList<ClientPresentationModel> observableListOfPortfolios = FXCollections.observableArrayList()

    ClientPresentationModel visiblePortfolio

    void mvcGroupInit(Map<String, Object> args) {
        visiblePortfolio = app.bindings.dolphin.presentationModel(SELECTED, portfolioId: null)
    }
}
