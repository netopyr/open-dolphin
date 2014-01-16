package demo

import org.opendolphin.core.server.EventBus
import org.opendolphin.demo.teammember.TeamEvent

class TeammemberController {

    EventBus teamBus

    def release() {
        teamBus.publish(null, new TeamEvent("release", null))
        render text:"done"
    }
}
