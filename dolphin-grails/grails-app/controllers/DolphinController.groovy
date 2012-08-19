import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.*
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.action.SwitchPresentationModelAction
import com.canoo.dolphin.core.server.comm.ServerConnector
import grails.converters.JSON

class DolphinController {

    // todo dk: a case for a OpenSessionInViewInterceptor (?)

    ServerDolphin checkDolphinInSession() {
        def dolphin = session.dolphin
        if ( ! dolphin){
            println "new in session"
            dolphin = new ServerDolphin(new ModelStore(), new ServerConnector())
            dolphin.registerDefaultActions()
            dolphin.serverConnector.register(new CustomAction(dolphin.modelStore)) // todo dk: make application dependent later
            session.dolphin = dolphin
        }
        return dolphin
    }

    // *** Standard actions

    def attributeCreated() {
        render text: populateAndExecute(new AttributeCreatedCommand())
    }
    def valueChanged() {
        render text: populateAndExecute(new ValueChangedCommand())
    }
    def switchPm() {
        populateAndExecute(new SwitchPresentationModelCommand())
        render text: ([] as JSON)
    }

    // *** Custom actions

    def pullVehicles() {
        render text: populateAndExecute(new NamedCommand(id:'pullVehicles'))
    }

    def longPoll() {
        render text: populateAndExecute(new NamedCommand(id:'longPoll'))
    }

    protected String populateAndExecute(command) {
        params.each {key, value ->
            if (key in 'action controller'.tokenize()) return
            if (key == 'attributeId') { value = value.toLong() }
            command[key] = value
        }
        log.debug(command)

        def result = checkDolphinInSession().serverConnector.receive(command)
        result as JSON
    }
}
