package com.canoo.dolphin.core.comm

class PullPmAttributesCommand extends Command {

    String pmId

    String toString() { super.toString() + " from pm $pmId"}
}
