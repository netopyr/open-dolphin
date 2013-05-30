package org.opendolphin.demo.crud

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.opendolphin.core.client.ClientPresentationModel

class PortfolioEditorModel {
    ClientPresentationModel portfolioPM

    ObservableList<ClientPresentationModel> observableListOfPositions = FXCollections.observableArrayList()
}
