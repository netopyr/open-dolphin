import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.server.action.*

class BootStrap {

    ReceiverService receiverService

    def init = { servletContext ->
        // todo: this should later be per-session instead of global
        def modelStore = new ModelStore()
        // compare InMemoryConfig
        [new StoreValueChangeAction(modelStore),
                new StoreAttributeAction(modelStore),
                new SwitchPresentationModelAction(modelStore),
                new CustomAction(modelStore), // application-specific action
        ].each { it.registerIn receiverService.receiver.registry }
    }

    def destroy = {
    }
}
