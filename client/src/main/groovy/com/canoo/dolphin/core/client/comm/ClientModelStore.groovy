package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.client.ClientPresentationModel
import java.util.concurrent.ConcurrentHashMap

class ClientModelStore {

    Map<String, ClientPresentationModel> modelStore = new ConcurrentHashMap<String, ClientPresentationModel>()// later, this may live somewhere else


}
