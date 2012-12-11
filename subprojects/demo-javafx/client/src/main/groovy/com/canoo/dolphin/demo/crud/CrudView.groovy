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

package com.canoo.dolphin.demo.crud
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ObservableList

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.binding.JavaFxUtil.value
import static com.canoo.dolphin.demo.crud.CrudConstants.PM_SELECTED_PORTFOLIO
import static groovyx.javafx.GroovyFX.start

class CrudView {

    @SuppressWarnings("GroovyAssignabilityCheck")
    static show(ClientDolphin clientDolphin) {

        ObservableList<ClientPresentationModel> observableListOfPortfolios = FXCollections.observableArrayList()

        // keeping track of the currently visible portfolio (the currently open tab) by
        // having a pm that captures the (dolphin) portfolio id
        // This is used on the server to find out the portfolio context for a named command.
        // A "mold" is not used in this case since "apply" produces too much overhead.
        def visiblePortfolio = clientDolphin.presentationModel(PM_SELECTED_PORTFOLIO, portfolioId: null )

        start { app ->
            stage {
                scene width: 1000, height: 600, stylesheets:"CrudDemo.css", {
                    splitPane  {
                        dividerPosition(index: 0, position: 0.2)
                        tableView id:'portfolios', selectionMode:"single", {
                            value 'name',  tableColumn('Portfolio', prefWidth: 100 )
                            value 'total', tableColumn('Total',     prefWidth: 40 )
                            value 'fixed', tableColumn('Fixed',     prefWidth: 40 )
                        }
                        stackPane {
                            text "Please select a Portfolio"
                            tabPane id:'portfolioTabs'
                        }
                    }
                }
            }


            portfolios.items = observableListOfPortfolios

            def sgb = delegate
            portfolios.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
                 visiblePortfolio.portfolioId.value = selectedPm.id

                def gotoTab = sgb.portfolioTabs.tabs.find { it.id == selectedPm.id }
                if (! gotoTab) {
                    def editor = new PortfolioEditor(selectedPm, clientDolphin)
                    gotoTab = sgb.tab id:selectedPm.id, {
                        editor.initView(sgb)
                    }
                    bind 'name' of selectedPm to 'text' of gotoTab
                    sgb.portfolioTabs.tabs << gotoTab
                }
                sgb.portfolioTabs.selectionModel.select(gotoTab)
            } as ChangeListener)



            clientDolphin.send CrudConstants.CMD_PULL_PORTFOLIOS, { portfolioPms ->
                for (pm in portfolioPms) {
                    observableListOfPortfolios << pm
                }
                fadeTransition(1.s, node: portfolios, to: 1).playFromStart()
            }



            primaryStage.show()
        }
    }
}
