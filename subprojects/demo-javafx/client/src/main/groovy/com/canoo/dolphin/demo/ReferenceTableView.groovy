package com.canoo.dolphin.demo

import com.canoo.dolphin.core.Attribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.util.Callback

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.style
import static com.canoo.dolphin.demo.ReferenceTableDemoProperties.PORTFOLIO_TYPE
import static groovyx.javafx.GroovyFX.start
import static com.canoo.dolphin.demo.ReferenceTableDemoProperties.CURRENCY
import com.canoo.dolphin.core.client.comm.WithPresentationModelHandler

class ReferenceTableView {
	private static final String TEXT = "text";

	static show() {
		start { app ->
			def currencies


			stage {
				scene {
					gridPane {
						label id: 'header', row: 0, column: 1,
								'Reference Table example'

						label id: 'currencyLabel', 'Currency: ', row: 1, column: 0
						textField id: 'currencyInput', row: 1, column: 1
						tableView(id: 'currencyListView', row: 2, column: 1, items: FXCollections.observableArrayList()) {
							col = tableColumn(text: 'Currency', prefWidth: 100)
						}
					}
				}
			}

			col.cellValueFactory = {it ->
				return new SimpleStringProperty(it.value.value)
			} as Callback

			style delegate

			ClientDolphin.clientModelStore.withPresentationModel ReferenceTableDemoProperties.CURRENCY_REF_TABLE, {ClientPresentationModel pm ->
				currencies = pm
				currencyListView.items.addAll(pm.getAttributes())
			} as WithPresentationModelHandler


			ClientDolphin.clientModelStore.withPresentationModel "${PORTFOLIO_TYPE}-1",  {ClientPresentationModel portfolio ->

				bind CURRENCY of portfolio to TEXT of currencyInput, {it ->
					if (currencies) {
						def attribute = currencies.findAttributeByPropertyName(it)
						def oldValue = currencyInput[TEXT]
						return attribute ? attribute.value : oldValue
					}
				}

				bind TEXT of currencyInput to CURRENCY of portfolio, {it ->
					def attribute
					for (Attribute attr : currencies.getAttributes()) {
						if (attr.propertyName == it || attr.value == it) {
							attribute = attr
						}
					}
					return attribute ? attribute.propertyName : ''
				}

				portfolio[CURRENCY].addPropertyChangeListener('value', { evt ->
					def attribute = currencies.findAttributeByPropertyName(evt.newValue)
					if (attribute) {
						currencyListView.selectionModel.select attribute
					}
					else {
						currencyListView.selectionModel.clearSelection()
					}
				} as PropertyChangeListener)

				currencyListView.selectionModel.selectedItemProperty().addListener({ o, oldVal, newVal ->
					if (newVal) {
						portfolio[CURRENCY].value = newVal.propertyName
					}
				} as ChangeListener)

			} as WithPresentationModelHandler





			primaryStage.show()
		}
	}

}
