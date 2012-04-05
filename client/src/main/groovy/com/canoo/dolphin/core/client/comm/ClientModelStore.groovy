package com.canoo.dolphin.core.client.comm

import com.canoo.dolphin.core.client.ClientPresentationModel
import java.util.concurrent.ConcurrentHashMap
import com.canoo.dolphin.core.client.ClientAttribute

class ClientModelStore {

    Map<String, ClientPresentationModel> modelStore = new ConcurrentHashMap<String, ClientPresentationModel>()// later, this may live somewhere else

    void register(ClientPresentationModel cpModel) {
            modelStore.put cpModel.id, cpModel
        }

    List<ClientAttribute> findAllClientAttributesById(long id) {
        modelStore.values().attributes.flatten().findAll { it.id == id } // todo: be more efficient
    }

    ClientAttribute findFirstAttributeById(id) {
        modelStore.values().attributes.flatten().find { it.id == id }
    }


    protected ClientPresentationModel findPmById(String id) {
        modelStore[id]
    }


    boolean containsPm(String id) {
        modelStore.containsKey(id)
    }


    void storePm(String id, ClientPresentationModel model) {
        modelStore[id] = model
    }

}
