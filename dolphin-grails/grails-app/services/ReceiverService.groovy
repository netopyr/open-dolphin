import com.canoo.dolphin.core.server.comm.Receiver
import com.canoo.dolphin.core.comm.Command
class ReceiverService {

    static transactional = false

    Receiver receiver = new Receiver()

    List<Command> receive(Command command) {
        receiver.receive(command)
    }
}
