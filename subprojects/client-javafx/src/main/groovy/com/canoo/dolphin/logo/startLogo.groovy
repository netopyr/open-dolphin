package com.canoo.dolphin.logo

import groovyx.javafx.GroovyFX

GroovyFX.start { app ->

    DolphinLogo dolphinLogo = new DolphinLogo()
    dolphinLogo.setPrefSize(401, 257)

    stage title: "Fun with Dolphin", {
        scene width:441, height: 297, {
            stackPane id:'stack'
        }
    }

    println dolphinLogo.skin //.dolphin.children

    stack.children.add(dolphinLogo)

    primaryStage.show()
}
