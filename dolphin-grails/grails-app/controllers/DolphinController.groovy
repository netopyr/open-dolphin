import com.canoo.dolphin.core.server.comm.ServerConnector

class DolphinController {

    DolphinSpringBean dolphinBean  // must be here in order to trigger creation
    ServerConnector serverConnector

    static allowedMethods = ['POST']

    def index() {
        def requestJson = request.inputStream.text
        log.debug "received json: $requestJson"
        def commands = serverConnector.codec.decode(requestJson)
        def results = new LinkedList()
        commands.each {
            log.debug "processing $it"
            results.addAll serverConnector.receive(it)
        }
        def jsonResponse = serverConnector.codec.encode(results)
        log.debug "sending json response: $jsonResponse"
        render text: jsonResponse
    }


}
