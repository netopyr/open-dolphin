
package com.canoo.dolphin.logo

import groovyx.javafx.GroovyFX

GroovyFX.start { app ->

    def effect = dropShadow(offsetY: 2, offsetX: 2, radius: 3, color:grey, input: lighting{distant(azimuth: -135.0)})
    logo = new DolphinLogo(width:401, height: 257, shuffle: true, effect: effect)

    stage title: "Tickle the Dolphin!", {
        scene width:441, height: 297, {
            logo.addTo delegate
        }
    }
    primaryStage.show()
}
