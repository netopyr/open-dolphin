package org.opendolphin.demo.crud

import org.opendolphin.core.ModelStoreEvent
import org.opendolphin.core.PresentationModel
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientPresentationModel
import org.opendolphin.demo.FX
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.chart.PieChart
import javafx.scene.control.TableView
import javafx.scene.Node

import java.beans.PropertyChangeListener

import static org.opendolphin.binding.JFXBinder.bind
import static org.opendolphin.binding.JavaFxUtil.cellEdit
import static org.opendolphin.binding.JavaFxUtil.value
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
import static javafx.scene.layout.GridPane.REMAINING

@SuppressWarnings("GroovyAssignabilityCheck")
class PortfolioEditor {

    ClientPresentationModel portfolioPM

    private Node viewer
    private ClientDolphin clientDolphin
    private ObservableList<ClientPresentationModel> observableListOfPositions  = FXCollections.observableArrayList()
    private plus, minus, nameField, tableBox, positions, totalField, fixedField, chart

    PortfolioEditor(ClientPresentationModel portfolioPM, ClientDolphin clientDolphin) {
        this.portfolioPM = portfolioPM
        this.clientDolphin = clientDolphin
    }

    Node initView(FactoryBuilderSupport builder) {
        if (! viewer) {
            viewer = createView builder
            bindings          builder
            attachListeners   builder
            pull              builder
        }
        return viewer
    }

    private Node createView(FactoryBuilderSupport builder) {
        Node result = null
        builder.with {
            result = gridPane hgap:10, vgap:12, padding: 20, {
                columnConstraints     minWidth: 80, halignment: "right"
                label       "Portfolio",    row: 0, column: 0
                nameField = textField       row: 0, column: 1, minHeight:32
                label       "Positions",    row: 1, column: 0
                tableBox  = vbox            row: 1, column: 1, {
                    positions = tableView   selectionMode:"single", editable:true, styleClass:'noBorder', id:'table', {
                        value INSTRUMENT, tableColumn('Instrument', editable:true,
                            prefWidth: builder.bind(table.width() * 2 / 3),
                            onEditCommit: cellEdit(INSTRUMENT, { it.toString() } ) )
                        value WEIGHT    , tableColumn('Weight',     editable:true,
                            prefWidth: builder.bind(table.width() / 3 - 1),
                            onEditCommit: cellEdit(WEIGHT,     { it.toInteger() } ) )
                    }
                    hbox {
                        plus  = button '+', styleClass:"bottomButton"
                        minus = button '-', styleClass:"bottomButton"
                    }
                }
                label       'Total',        row: 2, column: 0
                totalField = text           row: 2, column: 1
                label       'Fixed',        row: 3, column: 0
                fixedField = checkBox       row: 3, column: 1
                chart      = pieChart       row: 0, column: 2, rowSpan:REMAINING, animated: true
            }
            positions.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
            positions.items = observableListOfPositions
            result.opacity  = 0.3d
        }
        result
    }

    private void bindings(FactoryBuilderSupport builder) {
        builder.with {
            bind FIXED       of portfolioPM to FX.SELECTED  of fixedField
            bind FX.SELECTED of fixedField  to FIXED        of portfolioPM

            bind NAME        of portfolioPM to FX.TEXT      of nameField
            bind FX.TEXT     of nameField   to NAME         of portfolioPM

            bind TOTAL       of portfolioPM to FX.TEXT      of totalField

            [nameField, positions, plus, minus].each { control ->
                bind FIXED   of portfolioPM to FX.DISABLE  of control
            }
        }
    }

    private void attachListeners(FactoryBuilderSupport builder) {
        builder.with {
            def chart = chart // do not delete! Needed for local reference lookup.
            def observableListOfPositions = observableListOfPositions
            def positions = positions

            clientDolphin.addModelStoreListener POSITION, { ModelStoreEvent event ->
                PresentationModel position = event.presentationModel
                if (position[PORTFOLIO_ID].value != portfolioPM[DOMAIN_ID].value) return // only consider positions that refer to us
                switch (event.type){
                    case ModelStoreEvent.Type.ADDED:
                        observableListOfPositions << position
                        position[WEIGHT].addPropertyChangeListener(FX.VALUE, {
                            setCurrentPortfolio()
                            clientDolphin.send UPDATE
                        } as PropertyChangeListener)
                        def pieDataPoint = new PieChart.Data("",0)
                        bind INSTRUMENT of position to FX.NAME     of pieDataPoint
                        bind WEIGHT     of position to FX.PIE_VALUE of pieDataPoint, { it.toDouble() }

                        position[INSTRUMENT].addPropertyChangeListener(FX.VALUE, { // Workaround for http://javafx-jira.kenai.com/browse/RT-26845
                            def index = chart.data.indexOf pieDataPoint
                            def newDataPoint = new PieChart.Data(it.newValue, pieDataPoint.pieValue)
                            bind INSTRUMENT of position to FX.NAME     of newDataPoint
                            bind WEIGHT     of position to FX.PIE_VALUE of newDataPoint, { it.toDouble() }
                            chart.data[index] = newDataPoint       // consider unbinding pieDataPoint
                        } as PropertyChangeListener)

                        chart.data.add pieDataPoint
                        break
                    case ModelStoreEvent.Type.REMOVED:
                        def index = observableListOfPositions.indexOf position // assuming list and chartData have the same order
                        observableListOfPositions.remove position
                        chart.data.remove index
                        break
                }
            }

            plus.onAction {
                setCurrentPortfolio()
                clientDolphin.presentationModel(null, POSITION, instrument:'changeme', weight:10, portfolioId:portfolioPM[DOMAIN_ID].value)
                clientDolphin.send UPDATE
            }

            minus.onAction {
                def position = positions.selectionModel.selectedItem
                if (! position) return
                clientDolphin.delete(position)
                positions.selectionModel.clearSelection() // this may become a server decision
                setCurrentPortfolio()
                clientDolphin.send UPDATE
            }
        }
    }

    private void pull(FactoryBuilderSupport builder) {
        builder.with {
            setCurrentPortfolio()
            clientDolphin.send PULL, {
                clientDolphin.send UPDATE, {
                    fadeTransition(1.s, node: viewer, to: 1).playFromStart()
                }
            }
        }
    }

    def void setCurrentPortfolio() {
        def visiblePortfolio = clientDolphin.findPresentationModelById(SELECTED)
        visiblePortfolio[PORTFOLIO_ID].value = portfolioPM.id
    }
}
