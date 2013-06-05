package org.opendolphin.demo.crud

import javafx.beans.value.ChangeListener

import org.opendolphin.binding.JFXBinder
import org.opendolphin.demo.FX

import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.NAME

application(title: 'Portfolio Manager', sizeToScene: true, centerOnScreen: true) {
    scene(width: 1000, height: 600, stylesheets: 'CrudDemo.css') {
        splitPane {
            dividerPosition(index: 0, position: 0.2)
            vbox(alignment: 'top_center') {
                label('Portfolios', id: 'dierk')
                tableView(id: 'portfolios', selectionMode: 'single', vgrow: 'always') {
                    value('name', tableColumn('Portfolio', prefWidth: bind(portfolios.width() / 2)))
                    value('total', tableColumn('Total',    prefWidth: bind(portfolios.width() / 4)))
                    value('fixed', tableColumn('Fixed',    prefWidth: bind(portfolios.width() / 4)))
                }
            }
            stackPane {
                text('Please select a Portfolio', id: 'welcome')
                tabPane(id: 'portfolioTabs')
            }
        }
    }
}

portfolios.items = model.observableListOfPortfolios

portfolios.selectionModel.selectedItemProperty().addListener({ o, oldVal, selectedPm ->
    if (null == selectedPm) return // happens on deselect
    model.visiblePortfolio.portfolioId.value = selectedPm.id

    def gotoTab = portfolioTabs.tabs.find { it.id == selectedPm.id }
    if (!gotoTab) {
        def (m, v, c) = createMVCGroup('portfolioEditor', selectedPm.id, [tabId: selectedPm.id, portfolioPM: selectedPm])
        gotoTab = v.portfolioTab
        JFXBinder.bind NAME of selectedPm to FX.TEXT of gotoTab
        portfolioTabs.tabs << gotoTab
    }
    portfolioTabs.selectionModel.select(gotoTab)
} as ChangeListener)
