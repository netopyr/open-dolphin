package com.canoo.dolphin.core.comm

import groovy.transform.Canonical

@Canonical
class DeletePresentationModelCommand extends Command {
    String pmId

    String toString() {super.toString() + " pmId $pmId"}
}
