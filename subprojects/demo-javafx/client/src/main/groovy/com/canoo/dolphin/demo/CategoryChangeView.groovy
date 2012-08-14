package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientDolphin
import groovyx.javafx.SceneGraphBuilder
import javafx.event.EventHandler

import static com.canoo.dolphin.binding.JFXBinder.bind
import static groovyx.javafx.GroovyFX.start

class CategoryChangeView {

    static show(ClientDolphin clientDolphin) {

        def categories = [
                weight : [
                        (0..5)  : 'yellow',
                        (6..10) : 'red'
                ],
                size : [
                        20      : 'blue',
                        40      : 'green',
                      {it > 40} : 'black'
                ]
        ]

        def select  = clientDolphin.presentationModel('select', ['category'])

        def firstPm = clientDolphin.presentationModel('firstPm', ['weight','size'])
        firstPm.weight.value = 3
        firstPm.size.value = 20

        def secondPm = clientDolphin.presentationModel('secondPm', ['weight','size'])
        secondPm.weight.value = 10
        secondPm.size.value = 40

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage {
                scene width: 400, height: 300, {
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
            bind 'category' of select to 'fill' of first , mapper.curry(firstPm)            // alternative 1
            bind 'category' of select to 'fill' of second, { cat -> mapper(secondPm, cat) } // alternative 2

            bind 'size' of firstPm to 'width' of first, { size ->
                if (select.category?.value == 'size') first.fill = mapper(firstPm,'size')
                return size
            }

            first.onMouseClicked = { firstPm.size.value += 10 } as EventHandler

            primaryStage.show()
        }
    }
}
