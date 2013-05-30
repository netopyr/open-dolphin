package org.opendolphin.demo.crud

import griffon.transform.Threading

import javafx.scene.chart.PieChart
import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.PresentationModel
import org.opendolphin.demo.FX

import java.beans.PropertyChangeListener
import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.DOMAIN_ID
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.FIXED
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.NAME
import static org.opendolphin.demo.crud.PortfolioConstants.ATT.TOTAL
import static org.opendolphin.demo.crud.PortfolioConstants.CMD.UPDATE
import static org.opendolphin.demo.crud.PortfolioConstants.PM_ID.SELECTED
import static org.opendolphin.demo.crud.PositionConstants.ATT.INSTRUMENT
import static org.opendolphin.demo.crud.PositionConstants.ATT.PORTFOLIO_ID
import static org.opendolphin.demo.crud.PositionConstants.ATT.WEIGHT
import static org.opendolphin.demo.crud.PositionConstants.CMD.PULL
import static org.opendolphin.demo.crud.PositionConstants.TYPE.POSITION

class PortfolioEditorController {
    def model
    def builder
    String mvcName

    void mvcGroupInit(Map<String, Object> args) {
        this.mvcName = args.mvcName
        bindings()
        attachListeners()
        pull()
    }

    @Threading(Threading.Policy.SKIP)
    def plusAction = {
        setCurrentPortfolio()
        app.bindings.dolphin.presentationModel(null, POSITION, instrument: 'changeme', weight: 10, portfolioId: model.portfolioPM[DOMAIN_ID].value)
        app.bindings.dolphin.send UPDATE
    }

    @Threading(Threading.Policy.SKIP)
    def minusAction = {
        def position = builder.positions.selectionModel.selectedItem
        if (!position) return
        app.bindings.dolphin.delete(position)
        builder.positions.selectionModel.clearSelection() // this may become a server decision
        setCurrentPortfolio()
        app.bindings.dolphin.send UPDATE
    }

    // ==---------------------------------------------------------------------==

    private void bindings() {
        builder.with {
            bind FIXED       of model.portfolioPM to FX.SELECTED  of fixedField
            bind FX.SELECTED of fixedField        to FIXED        of model.portfolioPM

            bind NAME        of model.portfolioPM to FX.TEXT      of nameField
            bind FX.TEXT     of nameField         to NAME         of model.portfolioPM

            bind TOTAL       of model.portfolioPM to FX.TEXT      of totalField

            ['nameField', 'positions', 'plus', 'minus'].each { controlName ->
                bind FIXED   of model.portfolioPM to FX.DISABLE  of getVariable(controlName)
            }
        }
    }

    private void attachListeners() {
        builder.with {
            def chart = builder.chart // do not delete! Needed for local reference lookup.
            app.bindings.dolphin.addModelStoreListener POSITION, { ModelStoreEvent event ->
                PresentationModel position = event.presentationModel
                if (position[PORTFOLIO_ID].value != model.portfolioPM[DOMAIN_ID].value) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        model.observableListOfPositions << position
                        position[WEIGHT].addPropertyChangeListener(FX.VALUE, {
                            setCurrentPortfolio()
                            app.bindings.dolphin.send UPDATE
                        } as PropertyChangeListener)
                        def pieDataPoint = new PieChart.Data('',0)
                        bind INSTRUMENT of position to FX.NAME      of pieDataPoint
                        bind WEIGHT     of position to FX.PIE_VALUE of pieDataPoint, { it.toDouble() }

                        position[INSTRUMENT].addPropertyChangeListener(FX.VALUE, { // Workaround for http://javafx-jira.kenai.com/browse/RT-26845
                            def index = chart.data.indexOf pieDataPoint
                            def newDataPoint = new PieChart.Data(it.newValue, pieDataPoint.pieValue)
                            bind INSTRUMENT of position to FX.NAME      of newDataPoint
                            bind WEIGHT     of position to FX.PIE_VALUE of newDataPoint, { it.toDouble() }
                            chart.data[index] = newDataPoint       // consider unbinding pieDataPoint
                        } as PropertyChangeListener)

                        chart.data.add pieDataPoint
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        def index = model.observableListOfPositions.indexOf position // assuming list and chartData have the same order
                        model.observableListOfPositions.remove position
                        chart.data.remove index
                        break
                }
            }

            tab(portfolioTab, onClosed: { destroyMVCGroup(mvcName) })
        }
    }

    private void pull() {
        builder.with {
            setCurrentPortfolio()
            app.bindings.dolphin.send PULL, {
                app.bindings.dolphin.send UPDATE, {
                    fadeTransition(1.s, node: portfolioDisplay, to: 1).playFromStart()
                }
            }
        }
    }

    private void setCurrentPortfolio() {
        def visiblePortfolio = app.bindings.dolphin.findPresentationModelById(SELECTED)
        visiblePortfolio[PORTFOLIO_ID].value = model.portfolioPM.id
    }
}
