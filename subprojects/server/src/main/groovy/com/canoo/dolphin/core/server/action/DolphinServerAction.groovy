package com.canoo.dolphin.core.server.action

import com.canoo.dolphin.core.server.ServerDolphin

/**
 * Common superclass for all actions that need access to
 * the ServerDolphin, e.g. to work with the server model store.
 */
abstract class DolphinServerAction implements ServerAction {
    ServerDolphin serverDolphin
}
