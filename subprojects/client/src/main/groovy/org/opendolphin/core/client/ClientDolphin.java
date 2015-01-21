package org.opendolphin.core.client;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.Tag;
import org.opendolphin.core.client.comm.OnFinishedHandler;

import java.util.List;

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
}
