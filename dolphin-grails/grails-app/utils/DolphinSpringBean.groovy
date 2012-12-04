import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.JsonCodec
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.comm.ServerConnector
import com.canoo.dolphin.demo.CustomAction
import com.canoo.dolphin.demo.PerformanceAction
import groovy.util.logging.Log

@Log
class DolphinSpringBean {

    ServerDolphin dolphin

    DolphinSpringBean() {
        log.info "creating new dolphin for session"
        dolphin = new ServerDolphin(new ModelStore(), new ServerConnector(codec:new JsonCodec()))
        dolphin.registerDefaultActions()
        registerApplicationActions()
    }

    def void registerApplicationActions() {
        dolphin.serverConnector.register(new CustomAction(dolphin.modelStore))
		dolphin.serverConnector.register(new PerformanceAction(serverDolphin: dolphin))
    }
}
