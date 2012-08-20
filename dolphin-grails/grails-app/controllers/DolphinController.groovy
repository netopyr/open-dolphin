import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.*
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.comm.ServerConnector
import com.canoo.dolphin.demo.CustomAction

class DolphinController {

    static allowedMethods = ['POST']

    def index() {
        def dolphin = checkDolphinInSession()
        def requestJson = request.inputStream.text
        log.debug "received json: $requestJson"
        def commands = dolphin.serverConnector.codec.decode(requestJson)
        def results = new LinkedList()
        commands.each {
            log.debug "processing $it"
            results.addAll dolphin.serverConnector.receive(it)
        }
        def jsonResponse = dolphin.serverConnector.codec.encode(results)
        log.debug "sending json response: $jsonResponse"
        render text: jsonResponse
    }

    // todo dk: a case for a OpenSessionInViewInterceptor or a spring bean in session context

    ServerDolphin checkDolphinInSession() {
        def dolphin = session.dolphin
        if ( ! dolphin){
            log.info "creating new dolphin for session $session.id"
            dolphin = new ServerDolphin(new ModelStore(), new ServerConnector(codec:new JsonCodec()))
            dolphin.registerDefaultActions()
            registerApplicationActions(dolphin)
            session.dolphin = dolphin
        }
        return dolphin
    }

    def void registerApplicationActions(ServerDolphin dolphin) {
        dolphin.serverConnector.register(new CustomAction(dolphin.modelStore))
    }
}
