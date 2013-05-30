package org.opendolphin.demo.crud

import javafx.scene.control.TableView

import static org.opendolphin.binding.JavaFxUtil.cellEdit
import static org.opendolphin.binding.JavaFxUtil.value
import static org.opendolphin.demo.crud.PositionConstants.ATT.INSTRUMENT
import static org.opendolphin.demo.crud.PositionConstants.ATT.WEIGHT
import static javafx.scene.layout.GridPane.REMAINING

portfolioTab = tab(id: tabId) {
    portfolioDisplay = gridPane(hgap: 10, vgap: 12, padding: 20, opacity: 0.3d) {
        columnConstraints     minWidth: 80, halignment: 'right'
        label       'Portfolio',    row: 0, column: 0
        nameField = textField       row: 0, column: 1, minHeight:32
        label       'Positions',    row: 1, column: 0
        tableBox  = vbox            row: 1, column: 1, {
            positions = tableView   selectionMode: 'single', editable: true, styleClass: 'noBorder', id: 'table', {
                value INSTRUMENT, tableColumn('Instrument', editable: true,
                    prefWidth: bind(table.width() * 2 / 3),
                    onEditCommit: cellEdit(INSTRUMENT, { it.toString() } ) )
                value WEIGHT    , tableColumn('Weight',     editable: true,
                    prefWidth: bind(table.width() / 3 - 1),
                    onEditCommit: cellEdit(WEIGHT,     { it.toInteger() } ) )
            }
            hbox {
                plus  = button '+', styleClass: 'bottomButton', onAction: controller.plusAction
                minus = button '-', styleClass: 'bottomButton', onAction: controller.minusAction
            }
        }
        label       'Total',        row: 2, column: 0
        totalField = text           row: 2, column: 1
        label       'Fixed',        row: 3, column: 0
        fixedField = checkBox       row: 3, column: 1
        chart      = pieChart       row: 0, column: 2, rowSpan: REMAINING, animated: true
    }
}

positions.columnResizePolicy = TableView.CONSTRAINED_RESIZE_POLICY
positions.items = model.observableListOfPositions

portfolioTab