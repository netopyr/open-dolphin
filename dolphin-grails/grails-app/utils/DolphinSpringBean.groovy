import com.canoo.dolphin.core.server.EventBus
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.demo.CustomAction
import com.canoo.dolphin.demo.DemoTitlePurposeAction
import com.canoo.dolphin.demo.ManyEventsAction
import com.canoo.dolphin.demo.PerformanceAction
import com.canoo.dolphin.demo.SharedTachoAction
import com.canoo.dolphin.demo.VehiclePushActions
import com.canoo.dolphin.demo.crud.CrudActions
import com.canoo.dolphin.demo.crud.CrudService
import groovy.util.logging.Log

@Log
class DolphinSpringBean {

    DolphinSpringBean(
        ServerDolphin dolphin,
        CrudService crudService,
        EventBus tachoBus,
        EventBus manyEventsBus
    ) {
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

    }
}
