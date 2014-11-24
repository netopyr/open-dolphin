package org.opendolphin.binding

import javafx.collections.FXCollections
import javafx.embed.swing.JFXPanel
import javafx.event.EventType
import javafx.scene.control.TableColumn
import javafx.scene.control.TablePosition
import javafx.scene.control.TableView
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.core.client.ClientPresentationModel

class JavaFxUtilTests extends GroovyTestCase {
    static {
        new JFXPanel()
    }

    void testValue() {
        TableColumn column = new TableColumn()
        JavaFxUtil.value('attr', column)
        ClientAttribute attribute = new ClientAttribute('attr', 'content')
        assert column.cellValueFactory
        def result = column.cellValueFactory.call(new TableColumn.CellDataFeatures(null, column, [attr: attribute]))
        assert 'content' == result.value
    }

    void testCellEdit() {
        ClientPresentationModel model = new ClientPresentationModel([new ClientAttribute('attr', 0)])
        TableView view = new TableView(FXCollections.observableList([model]))
        TableColumn firstColumn = new TableColumn('firstColumn')
        view.getColumns().add(firstColumn)
        def editClosure = JavaFxUtil.cellEdit('attr', { value ->
            return "convertedValue $value"
        })
        def newValue = 'newValue'
        def result = editClosure.call(new TableColumn.CellEditEvent(view, new TablePosition(view, 0, firstColumn), new EventType<TableColumn.CellEditEvent>(), newValue))
        assert 'convertedValue newValue' == result
        assert 'convertedValue newValue' == model.getAt('attr').value
    }
}

