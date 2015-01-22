package org.opendolphin.core.client;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.comm.ClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;
import java.util.Map;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public interface ClientDolphin extends Dolphin<ClientAttribute, ClientPresentationModel> {

    ClientPresentationModel presentationModel(String id, List<String> attributeNames);

    ClientPresentationModel presentationModel(String id, String presentationModelType, ClientAttribute... attributes);

    ClientPresentationModel presentationModel(String id, ClientAttribute... attributes);

    void send(String commandName, OnFinishedHandler onFinished);

    void send(String commandName);

    void sync(Runnable runnable);

    ApplyToAble apply(ClientPresentationModel source);

    void delete(ClientPresentationModel modelToDelete);

    void deleteAllPresentationModelsOfType(String presentationModelType);

    ClientAttribute tag(ClientPresentationModel model, String propertyName, Tag tag, Object value);

    void addAttributeToModel(ClientPresentationModel presentationModel, ClientAttribute attribute);

    ClientPresentationModel copy(ClientPresentationModel sourcePM);

    void startPushListening(String pushActionName, String releaseActionName);

    void stopPushListening();

    boolean isPushListening();

    ClientConnector getClientConnector();

    void setClientConnector(ClientConnector connector);

    void setClientModelStore(ClientModelStore store);

    ClientPresentationModel createPresentationModel(List<ClientAttribute> attributes);

    ClientPresentationModel createPresentationModel(String id, List<ClientAttribute> attributes);

    @Deprecated
    ClientAttribute createAttribute(String propertyName);

    ClientAttribute createAttribute(String propertyName, Object initialValue, String qualifier, Tag tag);

    ClientAttribute createAttribute(String propertyName, Object initialValue, Tag tag);

    ClientAttribute createAttribute(String propertyName, Object initialValue, String qualifier);

    ClientAttribute createAttribute(String propertyName, Object initialValue);

    @Deprecated
    ClientAttribute createAttribute(Map props);
}
