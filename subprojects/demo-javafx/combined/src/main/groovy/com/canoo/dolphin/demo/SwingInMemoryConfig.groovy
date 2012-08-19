package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.comm.UiThreadHandler
import com.canoo.dolphin.core.comm.DefaultInMemoryConfig

import javax.swing.SwingUtilities

class SwingInMemoryConfig extends DefaultInMemoryConfig {

    SwingInMemoryConfig() {
        clientDolphin.clientConnector.uiThreadHandler = { todo -> SwingUtilities.invokeLater { todo() } } as UiThreadHandler
        serverDolphin.registerDefaultActions()
    }

}
