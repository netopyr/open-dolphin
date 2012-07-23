package com.canoo.dolphin.core.comm

class SwitchPresentationModelCommand extends Command {

    String pmId
    String sourcePmId

    String toString() { super.toString() + " $pmId to attributes of  $sourcePmId"}
}
