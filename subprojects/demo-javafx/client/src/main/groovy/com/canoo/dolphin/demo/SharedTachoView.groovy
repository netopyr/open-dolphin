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

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientDolphin
import jfxtras.labs.scene.control.gauge.Radial
import jfxtras.labs.scene.control.gauge.StyleModel

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start
import static jfxtras.labs.scene.control.gauge.Gauge.FrameDesign.CHROME

/**
 * This demo shows how to use the publish-subscribe pattern with the help of the Dolphin
 * event bus. Run multiple instances of it, where one instance is started with the command
 * line argument "Driver" to enable the slider that allows changing the speed.
 * Observe how changing the speed updates all views quasi-instantly.
 */

class SharedTachoView {

    static show(ClientDolphin dolphin, List driver) {

        start { app ->
            def gauge = new Radial(
                styleModel: new StyleModel(frameDesign: CHROME),
                title: "km/h",
                prefWidth: 250, prefHeight: 250,
                effect: dropShadow(radius: 20, color: rgba(0, 0, 0, 0.4))
            )

            stage title: "Dolphin shared tacho demo", {
                scene width: 400, height: 400, {
                    borderPane {
                        center margin: 10, { node gauge }
                        bottom margin: 40, { slider id: 'slider' }
                    }
                }
            }
            blueStyle delegate

            def car = dolphin.presentationModel 'Train', speed: 0
            car.speed.qualifier = "train.speed"

            bind 'speed' of car to FX.VALUE of gauge
            bind 'speed' of car to FX.VALUE of slider

            if (driver) {
                bind FX.VALUE of slider to 'speed' of car
            } else {
                slider.disabled = true
                Closure longPoll
                longPoll = { dolphin.send "poll.train.speed", longPoll }
                longPoll()
            }

            primaryStage.show()
        }
    }
}
