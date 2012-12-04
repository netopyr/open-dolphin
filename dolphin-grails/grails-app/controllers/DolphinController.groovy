import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.*
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.comm.ServerConnector
import com.canoo.dolphin.demo.CustomAction
import com.canoo.dolphin.demo.PerformanceAction

class DolphinController {

    DolphinSpringBean dolphinBean

    static allowedMethods = ['POST']

    def index() {
        def dolphin = dolphinBean.dolphin
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


}
