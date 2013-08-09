package org.opendolphin.demo

class MasterDetailConstants {
    static final String CMD_PULL    = unique 'pullSoccerPlayers'

    static final String TYPE_SOCCERPLAYER = unique('soccerPlayer')

    static final String ATT_RANK = 'rank'
    static final String ATT_NAME = 'name'


    static String unique(String part) {
        MasterDetailConstants.name + '-' + part
    }

    static String qualify(String id, String attributeName) {
        unique id + '.' + attributeName
    }
}
