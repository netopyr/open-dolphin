package com.canoo.dolphin.demo

import com.canoo.dolphin.core.client.ClientAttribute
import com.canoo.dolphin.core.client.ClientPresentationModel
import com.canoo.dolphin.core.client.ClientDolphin
import groovy.swing.SwingBuilder

import static com.canoo.dolphin.binding.JFXBinder.bind
import static com.canoo.dolphin.demo.MyProps.TEXT
import static com.canoo.dolphin.demo.MyProps.TITLE

class SwingView {
    void show(ClientDolphin clientDolphin) {

        def pm = createPresentationModel(clientDolphin)

        SwingBuilder builder = new SwingBuilder()
        builder.build {
            frame id:'primaryStage', visible: true, pack: true, {
                vbox {
                    label id: 'header'
                    label id: 'label'
                    textField id: 'input',
                           actionPerformed: { pm.title.value = input.text }
                    button id: 'submit', "Update labels and title",
                           actionPerformed: { pm.title.value = input.text }
                }
            }
        }
        bindPmToViews pm, builder
    }

    ClientPresentationModel createPresentationModel(ClientDolphin clientDolphin) {
        def titleAttr = new ClientAttribute(TITLE)
        titleAttr.value = "Some Text: <enter> or <submit>"
        def pm =  new ClientPresentationModel('demo', [titleAttr])
        clientDolphin.clientModelStore.add pm
        return pm
    }

    void bindPmToViews(ClientPresentationModel pm, builder) {
        builder.with {
            bind TITLE of pm to TITLE of primaryStage // groovy style

            bind(TITLE).of(pm).to(TEXT).of(label)       // java fluent-interface style

            bind TITLE of pm to TEXT of input

            // auto-update the header with every keystroke
            bind TEXT of input to TEXT of header        // todo dk: do we want to enable this?

            // the below is an alternative that updates the pm with every keystroke and thus all bound listeners
            // bind TEXT of input to TITLE of pm
        }
    }
}