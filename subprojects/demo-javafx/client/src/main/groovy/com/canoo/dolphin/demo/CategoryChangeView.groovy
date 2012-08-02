package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttributeWrapper
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.Dolphin
import com.canoo.dolphin.core.client.comm.OnFinishedHandler
import com.canoo.dolphin.core.comm.NamedCommand
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler
import javafx.scene.shape.Rectangle
import javafx.util.Callback

import java.beans.PropertyChangeListener

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static com.canoo.dolphin.demo.VehicleProperties.*
import static groovyx.javafx.GroovyFX.start

class CategoryChangeView {

    static show() {

        def categories = [
                weight : [
                        (0..5)  : 'yellow',
                        (6..10) : 'red'
                ],
                size : [
                        20      : 'blue',
                        40      : 'green'
                ]
        ]

        def select  = ClientPresentationModel.make('select', ['category'])

        def firstPm = ClientPresentationModel.make('firstPm', ['weight','size'])
        firstPm.weight.value = 3
        firstPm.size.value = 20
        def secondPm = ClientPresentationModel.make('secondPm', ['weight','size'])
        secondPm.weight.value = 10
        secondPm.size.value = 40

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 700, height: 500, {
                    borderPane {
                        top margin:10, {
                            choiceBox id:'choice', items:['weight','size'], {
                                onSelect { control, item -> select.category.value = item }
                            }
                        }
                        stackPane {
                            group effect: dropShadow(offsetY: 2, offsetX: 2, radius: 3, input: lighting{distant(azimuth: -135.0)}), {
                                rectangle(id: 'first',  x: 0,  y: 0, width: 40, height: 40, fill: white)
                                rectangle(id: 'second', x: 60, y: 0, width: 40, height: 40, fill: white)
            }   }   }   }   }


            def mapper = { pm, cat ->
                if (null == cat) return sgb.white
                def cases = categories[cat]
                if (null == cases) return sgb.white
                def entry = cases.find{ k,v -> pm[cat].value in k }
                if (null == entry) return sgb.white
                return sgb[entry.value]
            }
            bind 'category' of select to 'fill' of first , { cat -> mapper(firstPm,  cat) }
            bind 'category' of select to 'fill' of second, { cat -> mapper(secondPm, cat) }

            bind 'size' of firstPm to 'width' of first, { size ->
                if (select.category?.value == 'size') first.fill = mapper(firstPm,'size')
                return size
            }

            first.onMouseClicked = { firstPm.size.value += 10 } as EventHandler

            primaryStage.show()
        }
    }
}
