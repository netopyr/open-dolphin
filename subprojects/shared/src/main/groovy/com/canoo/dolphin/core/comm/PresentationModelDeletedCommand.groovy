package com.canoo.dolphin.core.comm

import groovy.transform.Canonical

@Canonical
class PresentationModelDeletedCommand extends Command {
    String pmId

    String toString() {super.toString() + " pmId $pmId"}
}
