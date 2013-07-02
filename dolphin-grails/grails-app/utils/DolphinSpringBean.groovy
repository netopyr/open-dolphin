import org.opendolphin.core.server.EventBus
import org.opendolphin.core.server.ServerDolphin
import org.opendolphin.demo.CustomAction
import org.opendolphin.demo.DemoTitlePurposeAction
import org.opendolphin.demo.ManyEventsAction
import org.opendolphin.demo.PerformanceAction
import org.opendolphin.demo.SharedTachoAction
import org.opendolphin.demo.VehiclePushActions
import org.opendolphin.demo.crud.CrudActions
import org.opendolphin.demo.crud.CrudService
//import org.opendolphin.demo.psycho.PsychodelicJavaActions
import groovy.util.logging.Log

import java.util.logging.Level
import java.util.logging.Logger

@Log
class DolphinSpringBean {

    DolphinSpringBean(
        ServerDolphin dolphin,
        CrudService crudService,
        EventBus tachoBus,
        EventBus manyEventsBus
    ) {

        Logger.getLogger("").level = Level.OFF

        log.info "creating new dolphin session"

        dolphin.registerDefaultActions()

        // todo dk: we may want to use dolphin.action cmdName, handler

        dolphin.register(new VehiclePushActions())
        dolphin.register(new CrudActions(crudService: crudService))
        dolphin.register(new DemoTitlePurposeAction())
        dolphin.register(new CustomAction())
        dolphin.register(new PerformanceAction())
        dolphin.register(new SharedTachoAction().subscribedTo(tachoBus))
        dolphin.register(new ManyEventsAction().subscribedTo(manyEventsBus))
 //       dolphin.register(new PsychodelicJavaActions())

    }
}
