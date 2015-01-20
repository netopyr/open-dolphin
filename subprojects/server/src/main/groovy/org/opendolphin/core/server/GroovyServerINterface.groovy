package org.opendolphin.core.server

import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.NamedCommandHandler

/**
 * Created by hendrikebbers on 20.01.15.
 */
public interface GroovyServerINterface {

    void registerDefaultActions()

    void register(DolphinServerAction action)

    void action(String name, NamedCommandHandler namedCommandHandler)

    ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto)

    boolean remove(ServerPresentationModel pm)

    void removeAllPresentationModelsOfType(String type)

    ServerPresentationModel getAt(String pmId)

    ServerPresentationModel findPresentationModelById(String id)

    ServerAttribute findAttributeById(String id)

    List<ServerPresentationModel> findAllPresentationModelsByType(String presentationModelType)

    List<ServerAttribute> findAllAttributesByQualifier(String qualifier)

    Collection<ServerPresentationModel> listPresentationModels()
}