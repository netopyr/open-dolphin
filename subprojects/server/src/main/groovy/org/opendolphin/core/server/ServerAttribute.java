package org.opendolphin.core.server;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;

/**
 * Created by hendrikebbers on 21.01.15.
 */
public interface ServerAttribute extends Attribute  {

    void silently(Runnable applyChange);

    ServerPresentationModel getPresentationModel();
}
