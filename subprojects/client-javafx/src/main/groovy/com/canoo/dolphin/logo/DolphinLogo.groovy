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

package com.canoo.dolphin.logo

import groovyx.javafx.SceneGraphBuilder
import javafx.scene.shape.Path

class DolphinLogo {
    long width, height
    boolean shuffle = false
    def effect = null

    def addTo(SceneGraphBuilder builder) {
        List<Path> strokes = new DolphinLogoPaths(width, height).paths()
        builder.stackPane {
            rectangle x: 0, y: 0, width: width, height: height, opacity: 0d
            allAnimations = parallelTransition()
            group id: 'dolphinLogoStrokes', effect: effect, {
                for (stroke in strokes) {
                    path(stroke, rotate: shuffle ? Math.random() * 360 : 0) {
                        allAnimations.children <<
                            rotateTransition(3.s, to: 0)
                    }
                }
            }
            onMouseClicked { allAnimations.playFromStart() }
        }
    }
}