import com.canoo.dolphin.core.comm.*
import com.canoo.dolphin.core.server.action.SwitchPresentationModelAction
import grails.converters.JSON

class CommandController {

    ReceiverService receiverService

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

        def result = receiverService.receive(command)
        result as JSON
    }
}
