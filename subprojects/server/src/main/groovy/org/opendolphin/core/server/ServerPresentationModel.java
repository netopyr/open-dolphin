package org.opendolphin.core.server;

import org.opendolphin.core.PresentationModel;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public interface ServerPresentationModel extends PresentationModel<ServerAttribute> {

    void syncWith(ServerPresentationModel sourcePresentationModel);

    void addAttribute(ServerAttribute attribute);

    void rebase();

    ServerModelStore getServerModelStore();

}
