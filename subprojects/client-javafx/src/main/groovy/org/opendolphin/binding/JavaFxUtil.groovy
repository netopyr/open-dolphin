package org.opendolphin.binding

import org.opendolphin.core.client.ClientAttributeWrapper
import javafx.scene.control.TableColumn
import javafx.util.Callback

class JavaFxUtil {

    /**
     * Register a cell value factory on the column that uses a ClientAttributeWrapper for
     * the property of the given name of the presentation model that represents the row data.
     * @return the column itself for convenient use in builders
     */
    static TableColumn value(String propertyName, TableColumn column) {
        column.cellValueFactory = { row -> new ClientAttributeWrapper(row.value[propertyName]) } as Callback
        return column
    }

    static Closure cellEdit(String propertyName, Closure convert ) {
        { event ->
            def positionPm = event.tableView.items.get(event.tablePosition.row)
            positionPm[propertyName].value = convert(event.newValue)
        }
    }
}
