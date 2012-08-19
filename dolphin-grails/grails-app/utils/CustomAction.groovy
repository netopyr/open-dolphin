
import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.PresentationModel
import com.canoo.dolphin.core.comm.CreatePresentationModelCommand
import com.canoo.dolphin.core.comm.GetPresentationModelCommand
import com.canoo.dolphin.core.comm.InitializeAttributeCommand
import com.canoo.dolphin.core.comm.NamedCommand
import com.canoo.dolphin.core.comm.ValueChangedCommand
import com.canoo.dolphin.core.server.ServerAttribute
import com.canoo.dolphin.core.server.ServerPresentationModel
import com.canoo.dolphin.core.server.action.ServerAction
import com.canoo.dolphin.core.server.comm.ActionRegistry

import static VehicleProperties.*

class CustomAction implements ServerAction {
    private final ModelStore modelStore

    CustomAction(ModelStore modelStore) {
        this.modelStore = modelStore
    }

    private Closure impl = { propertyName, NamedCommand command, response ->
        def actual = modelStore.findPresentationModelById('actualPm')
        def att = actual.findAttributeByPropertyName(propertyName)

        response << new ValueChangedCommand(attributeId: att.id, oldValue: att.value, newValue: "from server")
    }

    void registerIn(ActionRegistry registry) {
        def vehicles = ['red', 'blue', 'green', 'orange']
        def rand = { (Math.random() * 350).toInteger() }
        registry.register 'setTitle', impl.curry('title')
        registry.register 'setPurpose', impl.curry('purpose')
        registry.register 'pullVehicles', { NamedCommand command, response ->
            vehicles.each { String pmId ->
                response <<  new InitializeAttributeCommand(pmId:pmId, pmType:'vehicle', propertyName: X,      newValue: rand(), qualifier: "vehicle-${ pmId }.x")
                response <<  new InitializeAttributeCommand(pmId:pmId, pmType:'vehicle', propertyName: Y,      newValue: rand(), qualifier: "vehicle-${pmId}.y")
                response <<  new InitializeAttributeCommand(pmId:pmId, pmType:'vehicle', propertyName: WIDTH,  newValue: 80)
                response <<  new InitializeAttributeCommand(pmId:pmId, pmType:'vehicle', propertyName: HEIGHT, newValue: 25)
                response <<  new InitializeAttributeCommand(pmId:pmId, pmType:'vehicle', propertyName: ROTATE, newValue: rand(), qualifier: "vehicle-${pmId}.rotate")
                response <<  new InitializeAttributeCommand(pmId:pmId, pmType:'vehicle', propertyName: COLOR,  newValue: pmId,   qualifier: "vehicle-${pmId}.color")
            }
        }
        registry.register 'longPoll', { NamedCommand command, response ->
            sleep((Math.random() * 1000).toInteger()) // long-polling: server sleeps until new info is available
            Collections.shuffle(vehicles)
            def pm = modelStore.findPresentationModelById(vehicles.first())

            println "pm is $pm"

            if (!pm) return

            try {
                response << pm[X].changeValueCommand(rand())
                response << pm[Y].changeValueCommand(rand())
                response << pm[ROTATE].changeValueCommand(rand())
            }
            catch (e) {
                println pm.attributes*.propertyName
                println pm.id
                println modelStore
            }

        }
        registry.register 'pullTasks', { NamedCommand command, response ->
            vehicles.each {
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "description", newValue: rand())
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "fill", qualifier: "vehicle-${it}.color")
                response << new InitializeAttributeCommand(pmId: "TaskFor " + it, propertyName: "x", qualifier: "vehicle-${it}.x")
            }
        }

        registry.register GetPresentationModelCommand, { GetPresentationModelCommand command, response ->
            switch (command.pmType) {
                case 'vehicleDetail':
                    def pmId = command.pmId
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: WIDTH, newValue: rand(),)
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: X, qualifier: "vehicle-${command.selector}.x")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: Y, qualifier: "vehicle-${command.selector}.y")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: ROTATE, qualifier: "vehicle-${command.selector}.rotate")
                    response << new InitializeAttributeCommand(pmId: pmId, propertyName: COLOR, qualifier: "vehicle-${command.selector}.color")
                    break
            }
        }
    }

    private InitializeAttributeCommand newAttribute(Map params) {
        InitializeAttributeCommand attribute = new InitializeAttributeCommand()
        params.each { key, value -> attribute[key] = value }
        attribute
    }
}

class VehicleProperties {

    static String X = "x"
    static String Y = "y"
    static String WIDTH  = "width"
    static String HEIGHT = "height"
    static String ROTATE = "rotate"
    static String COLOR  = "fill"

}
