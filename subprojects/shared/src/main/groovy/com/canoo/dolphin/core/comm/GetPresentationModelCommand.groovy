package com.canoo.dolphin.core.comm

class GetPresentationModelCommand extends Command {

    String pmType
    String selector
    String getPmId() { "$pmType-$selector" }

    String toString() { super.toString() + " for presentation model of type $pmType and selector $selector" }
}
