package com.canoo.dolphin.core.comm

class GetPresentationModelCommand extends Command {

    String pmType
    String selector
    String getPmId() { "$pmType-$selector" }

    String toString() { super.toString() + " for view $pmType and discriminator $selector" }
}
