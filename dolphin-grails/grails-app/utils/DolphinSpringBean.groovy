import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.demo.CustomAction
import com.canoo.dolphin.demo.DemoTitlePurposeAction
import com.canoo.dolphin.demo.PerformanceAction
import com.canoo.dolphin.demo.crud.CrudActions
import com.canoo.dolphin.demo.crud.CrudService
import groovy.util.logging.Log

@Log
class DolphinSpringBean {

    DolphinSpringBean(ServerDolphin dolphin, CrudService crudService) {
        log.info "creating new dolphin session"

        dolphin.registerDefaultActions()

        dolphin.register(new CrudActions(crudService: crudService))
        dolphin.register(new DemoTitlePurposeAction())
        dolphin.register(new CustomAction())
        dolphin.register(new PerformanceAction())
    }
}
