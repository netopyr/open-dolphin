package org.opendolphin.core.client;

import org.opendolphin.core.PresentationModel;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public interface ClientPresentationModel extends PresentationModel<ClientAttribute> {

    boolean isClientSideOnly();

    void reset();
}
