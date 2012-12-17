import com.canoo.dolphin.core.ModelStore
import com.canoo.dolphin.core.comm.JsonCodec
import com.canoo.dolphin.core.server.ServerDolphin
import com.canoo.dolphin.core.server.comm.ServerConnector

beans = {

    modelStore(ModelStore) { bean ->
        bean.scope = 'session' // every session must have its own model store
    }

    serverConnector(ServerConnector) { bean ->
        bean.scope = 'session'  // could be shared among sessions but since the registry is set, this is safer...
        codec = new JsonCodec()
    }

    serverDolphin(ServerDolphin, ref('modelStore'), ref('serverConnector') ) { bean ->
        bean.scope = 'session'
    }

    dolphinBean(
        DolphinSpringBean, ref('serverDolphin'),
        ref('grailsCrudService')                    // more services would come here...
    ) { bean ->
        bean.scope = 'session'
    }
}
