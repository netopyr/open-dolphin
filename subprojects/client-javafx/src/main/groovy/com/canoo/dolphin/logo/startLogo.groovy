
package com.canoo.dolphin.logo

import groovyx.javafx.GroovyFX

GroovyFX.start { app ->
    boolean first=true

    def effect = dropShadow(offsetY: 2, offsetX: 2, radius: 3, color:grey, input: lighting{distant(azimuth: -135.0)})
    logo = new DolphinLogo(width:401, height: 257, shuffle: true, effect: effect)

    stage title: "Tickle the Dolphin!", {
        scene width:441, height: 297, {
            logo.addTo delegate
            circle id:"pulse", fill:transparent, stroke:rgb(207, 0, 58), strokeWidth: 3, opacity:0, translateX:-100, translateY: -20, effect:boxBlur(), {
                anim = timeline cycleCount: 3, {
                    onFinished { pulse.radius = 10 }
                    at (1.s) { change(pulse, "radius") to 200 tween ease_in }
                    at (1.s) { change(pulse, "opacity") to 0  tween linear  }
                }
            }
            onMouseClicked {
                if (first) { first = false; return }
                primaryStage.scene.lookup("#dolphinLogoStrokes").cache = true
                pulse.opacity = 1
                anim.play()
            }
        }
    }
    primaryStage.show()
}
