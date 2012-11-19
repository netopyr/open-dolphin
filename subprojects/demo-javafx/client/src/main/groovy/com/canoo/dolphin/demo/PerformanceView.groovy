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

import com.canoo.dolphin.LogConfig
import com.canoo.dolphin.core.ModelStoreEvent
import com.canoo.dolphin.core.client.ClientDolphin
import com.canoo.dolphin.core.client.ClientPresentationModel
import groovyx.javafx.SceneGraphBuilder
import javafx.beans.value.ChangeListener
import javafx.collections.ObservableList
import javafx.event.EventHandler

import static com.canoo.dolphin.binding.Binder.bind
import static com.canoo.dolphin.core.ModelStoreEvent.Type.ADDED
import static com.canoo.dolphin.core.ModelStoreEvent.Type.REMOVED
import static com.canoo.dolphin.demo.DemoStyle.blueStyle
import static groovyx.javafx.GroovyFX.start

/**
 * Measuring the response time when requesting so-many presentation models
 * from the server.
 */

class PerformanceView {

    static show(ClientDolphin dolphin) {

        def input = dolphin.presentationModel "input", count:0, time:0

        start { app ->
            SceneGraphBuilder sgb = delegate
            stage title: 'Measure Dolphin Response Times', {
                scene width: 350, height: 250, {
                    gridPane padding: 20, vgap:10, hgap:10, {
                        label "Number of PMs", row:0, column:0
                        textField id:'number', row:0, column:1, text:'1'
                        hbox row:1, column:1, spacing:10, {
                            button 'Request',id:'request'
                            button 'Clear',  id:'clear'
                        }

                        label "Last request (ms)", row:2, column:0
                        textField id:'time',       row:2, column:1

                        checkBox 'Show logs', id:'doLog', selected:true, row:3, column:1

                        label "Connector sleep (ms)", row:4, column:0
                        textField id:'conSleep',      row:4, column:1, text:dolphin.clientConnector.sleepMillis

                        label "PMs in store", row:5, column:0
                        label id:'store',     row:5, column:1, text:0

                    }
                }
            }

            bind 'time' of input to 'text' of time

            blueStyle sgb

            dolphin.addModelStoreListener { event ->
                if (event.type == ADDED)   store.text = store.text.toInteger() + 1
                if (event.type == REMOVED) store.text = store.text.toInteger() - 1
            }

            doLog.onAction {
                if (doLog.selected) {
                    LogConfig.logCommunication()
                } else {
                    LogConfig.noLogs()
                }
            }
            conSleep.text().addListener ({ obj, old, newVal ->
                dolphin.clientConnector.sleepMillis = conSleep.text.toInteger()
                println dolphin.clientConnector.sleepMillis
            } as ChangeListener)

			request.onAction {
                request.disable = true
                input.count.value = number.text
                long start = System.nanoTime()
                dolphin.send "stressTest", { pms ->
                    long end = System.nanoTime()
                    long ms = (end - start).intdiv 1000000
                    input.time.value = ms
                    request.disable = false
                }

			}
			clear.onAction {
                clear.disable = true
                long start = System.nanoTime()
                def all = dolphin.findAllPresentationModelsByType('all')
                def temp = new LinkedList(all)
                for (pm in temp) { dolphin.delete(pm) }
                dolphin.send "sync", { pms ->
                    long end = System.nanoTime()
                    long ms = (end - start).intdiv 1000000
                    input.time.value = ms
                    clear.disable = false
                }
			}
            primaryStage.show()
        }
    }

}
