package org.opendolphin.demo.sharedStation

import javafx.event.EventHandler
import javafx.stage.Stage
import org.opendolphin.core.Attribute
import org.opendolphin.core.client.ClientAttribute
import org.opendolphin.demo.FX

import java.beans.PropertyChangeListener

import static groovyx.javafx.GroovyFX.start
import org.opendolphin.core.client.ClientDolphin

import static org.opendolphin.binding.JFXBinder.bind

class SharedStationView {

    Stage           primaryStage
    ClientDolphin   dolphin
    List<String>    users   = "felicitas florian sophie elin".tokenize(' ')
    List<String>    actions = "wakeup play gotobed".tokenize(' ')
    List<String>    stati   = "asleep awake playing".tokenize(' ')


    def propertyMissing(String name) {
        primaryStage.scene.lookup("#$name")
    }

    void show(ClientDolphin dolphin) {
        this.dolphin = dolphin
        start { app ->
            createView(delegate)
            primaryStage = delegate.primaryStage
            createModels()
            binding()
            felicitas_button.fire()
            primaryStage.show()
        }
    }

    private void createView(sgb) {
        URL url = new File("client/src/main/resources/org/opendolphin/demo/sharedStation/SharedStation.fxml").toURI().toURL()
        sgb.stage title: "Shared Workstation", {
            scene {
                fxml url
            }
        }
    }

    private void createModels() {
        users.each {
            dolphin.presentationModel(it, "user",
                new ClientAttribute("name", it, "$it-name"),
                new ClientAttribute("status", "asleep", "$it-status"),
                new ClientAttribute("wakeup", true, "$it-wakeup-enabled"),
                new ClientAttribute("play",   false,"$it-play-enabled"),
                new ClientAttribute("gotobed",false,"$it-gotobed-enabled")
            )
        }
        dolphin.presentationModel("current_user", "user", name: null, status:null, wakeup:false, play:false, gotobed:false)

        for (user in users) {
            for (status in stati) {
                dolphin.presentationModel("${user}-${status}", "Detail", new ClientAttribute('detail','',"${user}-${status}-detail"))
            }
        }
        dolphin.presentationModel("current_detail", "Detail", detail:'')
        dolphin.presentationModel("user_status", null, content:null)
    }

    private void binding() {
        def current_user   = dolphin["current_user"]
        def current_detail = dolphin["current_detail"]
        def user_status    = dolphin["user_status"]

        user_status.content.addPropertyChangeListener(Attribute.VALUE, { event ->
            def current_user_detail = dolphin["${current_user.name.value}-${current_user.status.value}"]
            if (!current_user_detail) return // values are null on startup
            dolphin.apply(current_user_detail).to(current_detail)
        } as PropertyChangeListener)

        def update_user_status = {
            user_status.content.value = "${current_user.name.value}-${current_user.status.value}"
            return it
        }

        bind "name"   of current_user to FX.TEXT of user_label,   update_user_status
        bind "status" of current_user to FX.TEXT of status_label, update_user_status

        bind "detail" of current_detail to FX.TEXT of word_label
        bind "detail" of current_detail to FX.TEXT of input_textfield
        bind FX.TEXT  of input_textfield to "detail" of current_detail

        users.each { user ->
            def button = this."${user}_button"
            button.onAction = { dolphin.apply(dolphin[user]).to(current_user) } as EventHandler
        }
        actions.each { action ->
            def button = this."${action}_button"
            button.onAction = { dolphin.send(action) } as EventHandler
            bind action of current_user to FX.DISABLE of button, { !it }
        }
    }
}
