package com.canoo.dolphin.core.comm

class GetPresentationModelCommand extends Command {
    String pmId

    String toString() { super.toString() + " for presentation model for id $pmId" }
}
